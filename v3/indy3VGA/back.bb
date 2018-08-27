AppTitle "Indy3VGA Hintergrund"
win = CreateWindow("Indy3VGA Hintergrund", 150, 100, 350, 300, 0, 1)
button = CreateButton("Raum...", 10, 10, 65, 25, win) 
panel = CreatePanel(10, 45, 320, 215, win)
can = CreateCanvas(0, 0, 1280, 200, panel): SetBuffer CanvasBuffer(can)
slider = CreateSlider(0, 200, 320, 15, panel): SetSliderRange slider, 320, 1280
ClsColor 192, 192, 192: Cls
Dim pal(255, 2)
Global back, pos, stream

Repeat
	Select WaitEvent()
		Case $401
			Select EventSource()
				Case button Gosub paint
				Case slider SetGadgetShape can, -SliderValue(slider), 0, 1280, 200
			End Select
		Case $803 End
	End Select
Forever

.paint
	file$ = RequestFile("00/98/99 sind keine Raumdateien!", "LFL")
	If file$ = "" Then Return
	Cls

	back = ReadFile(file$)
		SeekFile back, 12
		width = ReadShort(back) / 8 - 1: height = ReadShort(back) - 1
		;^Breite in Streifen zu 8 Pixel aufteilen

		While Not ReadByte(back) = Asc("P") And ReadByte(back) = Asc("A")
			If Eof(back) Then Notify "Farbtabelle nicht gefunden": CloseFile back: Return 
			SeekFile(back, FilePos(back) - 1)
		Wend
		ReadShort(back) ;immer 256
		For i = 0 To 255
			pal(i, 0) = ReadByte(back) Shl 16 + ReadByte(back) Shl 8 + ReadByte(back)
			;^RGB-Daten
		Next

		off = FilePos(back) + 6
		SeekFile(back, FilePos(back) + 10)
		For i = 0 To width
			pal(i, 2) = ReadInt(back) + off ;Streifen-Offsets einlesen
		Next

		For i = 0 To width
			x = 0: y = 0
			SeekFile back, pal(i, 2) ;Springe zu Streifen
			Select ReadByte(back) ;Ermittle Kompressionsmethode
				Case 1 ;#1# Streifen komplett mittels Farbbytes einzeichnen 
					For x = 0 To 7
						For y = 0 To height
							col = ReadByte(back)
							WritePixel i * 8 + x, y, pal(col, 0)
							;            ^ Mahlnehmen um immer im aktuellen Streifen zu sein
						Next
					Next
  
				Case 2 ;#2# Bestimmte Anzahl Pixel mit gleicher Farbe zeichnen
					Repeat
						rep = ReadByte(back): col = ReadByte(back)
						For j = 0 To rep
							WritePixel i * 8 + x, y, pal(col, 0)
							If y = height
								y = 0: x = x + 1
								If x = 8 Then Exit ;An Streifenende kappen 
							Else
								y = y + 1
							EndIf
						Next
					Until x = 8

				Case 3 ;#3# Ein oder mehrere Pixel mittels Farbcode aus Bitstream zeichnen
					pos = 7: upDown = False
					Repeat
						sub = 0
						For j = 0 To 3
							sub = sub + bit() Shl j ;Ergibt Sub-Methode
						Next
						Select sub Shr 2
							Case 0
								col = 0
								For j = 0 To 3
									col = col + bit() Shl j
								Next
								For j = 0 To (sub And 3) + 1
									value = col + 16 * upDown
									WritePixel i * 8 + x, y, pal(value, 0)
									If y = height
										y = 0: x = x + 1
									Else
										y = y + 1
									EndIf
								Next
							Case 1
								For j = 0 To sub And 3
									col = 0
									For z = 0 To 3
										col = col + bit() Shl z
									Next
									value = col + 16 * upDown
									WritePixel i * 8 + x, y, pal(value, 0)
									If y = height
										y = 0: x = x + 1
									Else
										y = y + 1
									EndIf
								Next
							Case 2
								upDown = False
								For j = 0 To 3
									upDown = upDown + bit() Shl j
								Next
							Default Notify "Unbekannte Sub-Kompression": CloseFile back: Return
						End Select
					Until x = 8

				Case 4 ;#4# Festgelegte Farben einzeln, andere mit bestimmter Anzahl Pixel zeichnen  
					value = ReadByte(back)
					For j = 0 To value - 1
						pal(j, 1) = ReadByte(back)
					Next
					Repeat
						col = ReadByte(back)
						If col < value
							WritePixel i * 8 + x, y, pal(pal(col, 1), 0)
							If y = height
								y = 0: x = x + 1
							Else
								y = y + 1
							EndIf
						Else
							rep = col - value ;Ergibt Anzahl der zu zeichnenden Pixel in folgender Farbe 
							col = ReadByte(back)
							For j = 0 To rep
								WritePixel i * 8 + x, y, pal(col, 0)
								If y = height
									y = 0: x = x + 1
									If x = 8 Then Exit
								Else
									y = y + 1
								EndIf
							Next
						EndIf
					Until x = 8

				Case 7 ;#7# Einzelne Pixel durch Farbcode mittels Bitstream zeichnen
					col = ReadByte(back)
					WritePixel i * 8 + x, y, pal(col, 0)
					y = y + 1
					pos = 7: upDown = True
					Repeat
						For j = 0 To 2
							If bit() = 0 Then Exit
						Next
						Select j
							Case 0
							Case 1
								If upDown
									col = col + 1: upDown = False
								Else
									col = col - 1: upDown = True
								EndIf
							Case 2
								If upDown Then col = col - 1 Else col = col + 1
							Case 3
								col = 0: upDown = True
								For z = 0 To 7
									col = col + bit() Shl z
								Next
							Default Notify "Unbekannte Sub-Kompression": CloseFile back: Return
						End Select
						WritePixel i * 8 + x, y, pal(col, 0)
						If y = height
							y = 0: x = x + 1
						Else
							y = y + 1
						EndIf
					Until x = 8

				Default Notify "Unbekannte Kompression": CloseFile back: Return
			End Select
		Next
	CloseFile back
	FlipCanvas can
Return

Function bit()
	pos = pos + 1
	If pos = 8 Then stream = ReadByte(back): pos = 0
	Return (stream Shr pos) And 1
End Function