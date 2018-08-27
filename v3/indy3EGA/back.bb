win = CreateWindow("Indy3EGA Hintergrund", 150, 100, 350, 300, 0, 1)
button = CreateButton("Raum...", 10, 10, 65, 25, win)
panel = CreatePanel(10, 45, 320, 215, win)
can = CreateCanvas(0, 0, 1280, 200, panel): SetBuffer CanvasBuffer(can)
slider = CreateSlider(0, 200, 320, 15, panel): SetSliderRange slider, 320, 1280
ClsColor 240, 240, 240: Cls

Local pal[15] ;16 EGA-Standardfarben
Data $000000, $0000AA, $00AA00, $00AAAA, $AA0000, $AA00AA, $AA5500, $AAAAAA
Data $555555, $5555FF, $55FF55, $55FFFF, $FF5555, $FF55FF, $FFFF55, $FFFFFF
For i = 0 To 15: Read pal[i]: Next

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
		SeekFile back, 4
		width = ReadShort(back) Xor $FFFF: height = ReadShort(back) Xor $FFFF
		strip = width / 8 - 1 ;Hintergründe sind in Streifen zu 8 Pixel aufgeteilt

		SeekFile back, 10: old = SeekFile(back, (ReadShort(back) Xor $FFFF) + 2)
		off = old - 2
		For i = 0 To strip
			old = SeekFile(back, old) + 2
			SeekFile(back, (ReadShort(back) Xor $FFFF) + off)
			xpos = 0
			Repeat
				col = ReadByte(back) Xor $FF
				If col < 128
					rep = col Shr 4: col = col And $0F
					If rep = 0 Then rep = ReadByte(back) Xor $FF
					For j = 1 To rep
						WritePixel i * 8 + xpos, ypos, pal[col]
						;          ^Malnehmen um immer im aktuellen Streifen zu sein
						ypos = ypos + 1
						If ypos = height
							ypos = 0: xpos = xpos + 1
							If xpos = 8 Then Exit
						EndIf
					Next
				Else
					rep = col And %00111111
					If col And %01000000
						col = ReadByte(back) Xor $FF
						If rep = 0 Then rep = ReadByte(back) Xor $FF
						For j = 1 To rep
							If j And 1
								WritePixel i * 8 + xpos, ypos, pal[col Shr 4]
							Else
								WritePixel i * 8 + xpos, ypos, pal[col And $0F]
							EndIf
							ypos = ypos + 1
							If ypos = height
								ypos = 0: xpos = xpos + 1
								If xpos = 8 Then Exit
							EndIf
						Next
					Else
						If rep = 0 Then rep = ReadByte(back) Xor $FF
						For j = 1 To rep 
							If xpos Then WritePixelFast i * 8 + xpos, ypos, ReadPixel(i * 8 + xpos - 1, ypos)
							ypos = ypos + 1
							If ypos = height
								ypos = 0: xpos = xpos + 1
								If xpos = 8 Then Exit
							EndIf
						Next
					EndIf
				EndIf
			Until xpos = 8
		Next
	CloseFile back

	FlipCanvas can
Return