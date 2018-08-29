win = CreateWindow("MM v2 Hintergrund", 150, 100, 350, 300, 0, 1)
button = CreateButton("Raum...", 10, 10, 65, 25, win)
panel = CreatePanel(10, 45, 320, 215, win)
can = CreateCanvas(0, 0, 960, 200, panel): SetBuffer CanvasBuffer(can)
slider = CreateSlider(0, 200, 320, 15, panel): SetSliderRange slider, 320, 960
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
				Case slider SetGadgetShape can, -SliderValue(slider), 0, 960, 200
			End Select
		Case $803 End
	End Select
Forever

.paint
	file$ = RequestFile("00 ist keine Raumdatei!", "LFL")
	If file$ = "" Then Return
	Cls

	back = ReadFile(file$)
		SeekFile back, 4
		width = ReadShort(back) Xor $FFFF: height = ReadShort(back) Xor $FFFF

		SeekFile back, 10: SeekFile(back, ReadShort(back) Xor $FFFF)
		xpos = 0: ypos = 0
		Repeat
			col = ReadByte(back) Xor $FF
			If col < 128
				rep = col Shr 4: col = col And $0F
				If rep = 0 Then rep = ReadByte(back) Xor $FF
				For i = 1 To rep
					WritePixel xpos, ypos, pal[col]
					ypos = ypos + 1
					If ypos = height
						ypos = 0: xpos = xpos + 1
						If xpos = width Then Exit
					EndIf
				Next
			Else
				rep = col And %01111111
				If xpos = 0 Then Exit Else If rep = 0 Then rep = ReadByte(back) Xor $FF
				For i = 1 To rep
					If xpos Then WritePixel xpos, ypos, ReadPixel(xpos - 1, ypos)
					ypos = ypos + 1
					If ypos = height
						ypos = 0: xpos = xpos + 1
						If xpos = width Then Exit
					EndIf
				Next
			EndIf
		Until xpos = width
	CloseFile back

	FlipCanvas can
Return