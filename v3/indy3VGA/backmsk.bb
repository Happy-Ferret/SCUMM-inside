AppTitle "Indy3VGA Hintergrund-Maske"
win = CreateWindow("Indy3VGA Hintergrund-Maske", 200, 150, 345, 295, 0, 1)
button = CreateButton("Raum...", 10, 10, 65, 25, win) 
panel = CreatePanel(10, 45, 320, 215, win)
can = CreateCanvas(0, 0, 960, 200, panel): SetBuffer CanvasBuffer(can)
slider = CreateSlider(0, 200, 320, 15, panel): SetSliderRange slider, 320, 960
Local off[119]
Color 0, 0, 0: ClsColor 192, 192, 192: Cls

Repeat
	Select WaitEvent() 
		Case $401
			Select EventSource()
				Case button Gosub paint
				Case slider SetGadgetShape can, -SliderValue(slider), 0, 960, 200
			End Select
		Case $803 End
	End Select
Forever

.paint
	file$ = RequestFile("00/98/99 sind keine Raumdateien!", "LFL")
	If file$ = "" Then Return

	mask = ReadFile(file$)
		SeekFile mask, 12: width = ReadShort(mask): height = ReadShort(mask)
		While Not ReadByte(mask) = Asc("B") And ReadByte(mask) = Asc("M")
			If Eof(mask) Then Notify "Einstiegspunkt nicht gefunden": CloseFile mask: Return 
			SeekFile(mask, FilePos(mask) - 1)
		Wend

		offset = FilePos(mask) + ReadInt(mask): SeekFile mask, offset
		If ReadInt(mask) = 0 Then Notify "Raum hat keine Maske": CloseFile mask: Return
		For i = 0 To width / 8 - 1 ;Hintergrund ist in Streifen mit 8 Pixel Breite aufgeteilt
			strip = ReadShort(mask)
			If strip = 0 Then off[i] = 0 Else off[i] = offset + strip
			;^Streifen ohne Maske überspringen
		Next

		Cls: Rect 0, 0, width, height
		For i = 0 To width / 8 - 1
			If off[i]
				SeekFile mask, off[i]
				y = 0
				Repeat
					rep = ReadByte(mask)
					If rep < 127
						For j = 1 To rep
							col = ReadByte(mask)
							If col
								For x = 0 To 7
									If (col Shl x) And %10000000 Then WritePixel i * 8 + x, y, $FFFFFF
								Next
							EndIf
							y = y + 1
						Next
					Else
						rep = rep And %01111111
						col = ReadByte(mask)
						If col
							For j = 1 To rep
								For x = 0 To 7
									If (col Shl x) And %10000000 Then WritePixel i * 8 + x, y, $FFFFFF
								Next
								y = y + 1
							Next
						Else
							y = y + rep
						EndIf
					EndIf
				Until y = height
			EndIf
		Next
	CloseFile mask

	FlipCanvas can
Return