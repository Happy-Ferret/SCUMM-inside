AppTitle "SCUMM3 Font"
win = CreateWindow("SCUMM3 Font", 100, 100, 525, 270, 0, 3) 
area = CreateTextArea(15, 15, 150, 200, win)
can = CreateCanvas(175, 15, 320, 200, win): SetBuffer CanvasBuffer(can)
SetGadgetLayout area, 1, 0, 1, 1: SetGadgetLayout can, 1, 1, 1, 1

font = ReadFile("99.LFL") ;oder 98.LFL
	If Not font Then RuntimeError "Datei nicht gefunden!"

	SeekFile font, 6: keep = ReadByte(font) - 1 ;Anzahl der Zeichen
	AddTextAreaText area, "Anzahl der Zeichen["+(keep+1)+"]" +Chr$(10)+ "Höhe der Zeichen["+ReadByte(font)+"]" +Chr$(10)
	For i = 0 To keep
		AddTextAreaText area, "Zeichen["+i+"]  Breite["+ReadByte(font)+"]" +Chr$(10)
	Next

	For i = 0 To keep
		For j = 0 To 7 ;pro Zeichen 8 Byte
			px = ReadByte(font)
			For z = 0 To 7 ;1 Byte hat 8 Bit. Jedes Zeichen besteht aus einer 8x8 Matrix
				If px Shl z And %10000000 Then Plot x, y ;wenn Bit = 1 dann Pixel setzen
				x = x + 1
			Next
			y = y + 1
			If Not j = 7
				x = x - 8
			Else
				If x = 320
					x = 0
				Else
					y = y - 8
				EndIf
			EndIf
		Next
	Next
CloseFile font

Repeat Until WaitEvent() = $803