AppTitle "MM v2 Hintergrund-Maske"
win = CreateWindow("MM v2 Hintergrund-Maske", 200, 150, 345, 295, 0, 1)
button = CreateButton("Raum...", 10, 10, 65, 25, win) 
panel = CreatePanel(10, 45, 320, 215, win)
can = CreateCanvas(0, 0, 960, 200, panel): SetBuffer CanvasBuffer(can)
slider = CreateSlider(0, 200, 320, 15, panel): SetSliderRange slider, 320, 960
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
	file$ = RequestFile("00 ist keine Raumdatei!", "LFL")
	If file$ = "" Then Return

	mask = ReadFile(file$)
		SeekFile mask, 4: width = ReadShort(mask) Xor $FFFF: height = ReadShort(mask) Xor $FFFF
		SeekFile mask, 12: SeekFile(mask, ReadShort(mask) Xor $FFFF)
		Cls: Rect 0, 0, width, height

		x = 0
		Repeat
			col = ReadByte(mask) Xor $FF
			If col < 128
				rep = col
				If rep = 0 Then rep = ReadByte(mask) Xor $FF
				For i = 1 To rep
					col = ReadByte(mask) Xor $FF
					If col
						For j = 0 To 7
							If (col Shl j) And %10000000 Then WritePixel j + x, y, $FFFFFF
						Next
					EndIf
					y = y + 1
					If y = height
						x = x + 8: y = 0
					EndIf
				Next
			Else
				rep = col And %01111111
				If rep = 0 Then rep = ReadByte(mask) Xor $FF
				col = ReadByte(mask) Xor $FF
				For i = 1 To rep
					If col
						For j = 0 To 7
							If (col Shl j) And %10000000 Then WritePixel j + x, y, $FFFFFF
						Next
					EndIf
					y = y + 1
					If y = height
						x = x + 8: y = 0
					EndIf
				Next
			EndIf
		Until x = width
	CloseFile mask

	FlipCanvas can
Return