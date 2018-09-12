AppTitle "LoomEGA Hintergrund-Maske"
win = CreateWindow("LoomEGA Hintergrund-Maske", 200, 150, 345, 295, 0, 1)
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
	file$ = RequestFile("00 u. 99 sind keine Raumdateien!", "LFL")
	If file$ = "" Then Return

	mask = ReadFile(file$)
		SeekFile mask, 4: width = ReadShort(mask) Xor $FFFF: height = ReadShort(mask) Xor $FFFF

		SeekFile mask, 12: old = SeekFile(mask, ReadShort(mask) Xor $FFFF)
		Cls: Rect 0, 0, width, height
		off = old
		For i = 0 To width / 8 - 1 ;Hintergrund ist in Streifen mit 8 Pixel Breite aufgeteilt
			old = SeekFile(mask, old) + 2
			strip = ReadShort(mask) Xor $FFFF

			If strip
				SeekFile(mask, strip + off)
				y = 0
				Repeat
					rep = ReadByte(mask) Xor $FF
					If rep < 128
						For j = 1 To rep
							col = ReadByte(mask) Xor $FF
							If col
								For x = 0 To 7
									If (col Shl x) And %10000000 Then WritePixel i * 8 + x, y, $FFFFFF
								Next
							EndIf
							y = y + 1
						Next
					Else
						rep = rep And %01111111
						col = ReadByte(mask) Xor $FF
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