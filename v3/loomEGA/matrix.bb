win = CreateWindow("LoomEGA Matrix", 100, 100, 580, 400, 0, 1)
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
	file$ = RequestFile("00 u. 99 sind keine Raumdateien!", "LFL")	
	If file$ = "" Then Return
	SetTextAreaText boxArea, "": SetTextAreaText matrixArea, "": Cls

	boxmatrix = ReadFile(file$)
		SeekFile boxmatrix, 21: SeekFile(boxmatrix, ReadShort(boxmatrix) Xor $FFFF)
		keep = (ReadByte(boxmatrix) Xor $FF) - 1 ;Anzahl der Boxen
		For i = 0 To keep
			OLx = ReadShort(boxmatrix) Xor $FFFF: OLy = ReadShort(boxmatrix) Xor $FFFF
			ORx = ReadShort(boxmatrix) Xor $FFFF: ORy = ReadShort(boxmatrix) Xor $FFFF
			URx = ReadShort(boxmatrix) Xor $FFFF: URy = ReadShort(boxmatrix) Xor $FFFF
			ULx = ReadShort(boxmatrix) Xor $FFFF: ULy = ReadShort(boxmatrix) Xor $FFFF
			Line OLx, OLy, ORx, ORy: Line URx, URy, ULx, ULy
			Line OLx, OLy, ULx, ULy: Line ORx, ORy, URx, URy  
			AddTextAreaText boxArea, "Box["+i+"]  OL["+OLx+" x "+OLy+"]  OR["+ORx+" x "+ORy+"]" +Chr$(10)
			AddTextAreaText boxArea, "            UR["+URy+" x "+URy+"]  UL["+ULx+" x "+ULy+"]" +Chr$(10)
			AddTextAreaText boxArea, "            Maske["+(ReadByte(boxmatrix) Xor $FF)+"]  Art["+(ReadByte(boxmatrix) Xor $FF)+"]" +Chr$(10) +Chr$(10)
		Next

		For i = 0 To keep
			AddTextAreaText matrixArea, "["+i+"]"
			While Not ReadByte(boxmatrix) = 0
				SeekFile(boxmatrix, FilePos(boxmatrix) - 1)
				AddTextAreaText matrixArea, "  ("+(ReadByte(boxmatrix) Xor $FF)+"-"+(ReadByte(boxmatrix) Xor $FF)+")>"+(ReadByte(boxmatrix) Xor $FF)
			Wend
			AddTextAreaText matrixArea, Chr$(10)
		Next
	CloseFile boxmatrix
	FlipCanvas can 
Return