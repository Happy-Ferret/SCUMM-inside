win = CreateWindow("MM v1 Matrix", 100, 100, 580, 400, 0, 1)
button = CreateButton("Raum...", 10, 5, 65, 25, win) 
boxArea = CreateTextArea(10, 35, 220, 325, win)
matrixArea = CreateTextArea(240, 235, 320, 125, win)
panel = CreatePanel(240, 10, 320, 215, win)
can = CreateCanvas(0, 0, 960, 200, panel): SetBuffer CanvasBuffer(can)
slider = CreateSlider(0, 200, 320, 15, panel): SetSliderRange slider, 320, 960

Repeat
	Select WaitEvent() 
		Case $401
			Select EventSource()
				Case button Gosub load
				Case slider SetGadgetShape can, -SliderValue(slider), 0, 960, 200
			End Select
		Case $803 End
	End Select
Forever

.load
	file$ = RequestFile("00 ist keine Raumdatei!", "LFL")	
	If file$ = "" Then Return
	SetTextAreaText boxArea, "": SetTextAreaText matrixArea, "": Cls

	boxmatrix = ReadFile(file$)
		SeekFile boxmatrix, 21: SeekFile(boxmatrix, ReadByte(boxmatrix) Xor $FF)
		keep = (ReadByte(boxmatrix) Xor $FF) - 1 ;Anzahl der Boxen
		For i = 0 To keep
			Oy = (ReadByte(boxmatrix) Xor $FF) * 2: Uy = (ReadByte(boxmatrix) Xor $FF) * 2
			OLx = (ReadByte(boxmatrix) Xor $FF) * 8: ORx = (ReadByte(boxmatrix) Xor $FF) * 8
			ULx = (ReadByte(boxmatrix) Xor $FF) * 8: URx = (ReadByte(boxmatrix) Xor $FF) * 8
			;^ * = Nach C64 Standard
			Line OLx, Oy, ORx, Oy: Line ULx, Uy, URx, Uy
			Line OLx, Oy, ULx, Uy: Line ORx, Oy, URx, Uy
			AddTextAreaText boxArea, "Box["+i+"]  OL["+OLx+" x "+Oy+"]  OR["+ORx+" x "+Oy+"]" +Chr$(10)
			AddTextAreaText boxArea, "            UR["+URx+" x "+Uy+"]  UL["+ULx+" x "+Uy+"]" +Chr$(10)
			AddTextAreaText boxArea, "            Maske["+(ReadByte(boxmatrix) Xor $FF)+"]  Art["+(ReadByte(boxmatrix) Xor $FF)+"]" +Chr$(10) +Chr$(10)
		Next

		SeekFile(boxmatrix, FilePos(boxmatrix) + keep + 1) ;Matrix Offsets
		For i = 0 To keep
			AddTextAreaText matrixArea, "["+i+"]  "
			For j = 0 To keep
				AddTextAreaText matrixArea, (ReadByte(boxmatrix) Xor $FF)+" "
			Next
			AddTextAreaText matrixArea, Chr$(10)
		Next
	CloseFile boxmatrix
	FlipCanvas can 
Return