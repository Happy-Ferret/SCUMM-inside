AppTitle "Indy3VGA Palette"
win = CreateWindow("Indy3VGA Palette", 200, 100, 240, 340, 0, 1) 
button = CreateButton("Raum...", 10, 5, 65, 25, win)
area = CreateTextArea(10, 35, 215, 265, win)

Repeat
	Select WaitEvent()
		Case $401 If EventSource() = button Then Gosub load
		Case $803 End
	End Select
Forever

.load
	file$ = RequestFile("(00/98/99 sind keine Raumdateien!)", "LFL")
	If file$ = "" Then Return

	pal = ReadFile(file$)
		While Not ReadByte(pal) = Asc("P") And ReadByte(pal) = Asc("A")
			If Eof(pal) Then Notify "Einstiegspunkt nicht gefunden": CloseFile pal: Return 
			SeekFile(pal, FilePos(pal) - 1)
		Wend
		ReadShort(pal)

		SetTextAreaText area, ""
		For i = 0 To 255
			r = ReadByte(pal): g = ReadByte(pal): b = ReadByte(pal)
			AddTextAreaText area, Chr$(263)+ "   Farbe ["+i+"]   RGB ["+r+" - "+g+" - "+b+"]" +Chr$(10)
			FormatTextAreaText area, r, g, b, 1, TextAreaChar(area, i), 1
		Next
	CloseFile pal
Return