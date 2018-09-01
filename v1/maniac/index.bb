AppTitle "MM v1 Index"
win = CreateWindow("MM v1 Index", 200, 100, 230, 360, 0, 1) 
area = CreateTextArea(10, 10, 200, 310, win)
Local file[199]

index = ReadFile("00.LFL")
	If Not index Then RuntimeError "Datei nicht gefunden!"

	ReadShort(index) ;C64 Daten
	AddTextAreaText area, "Anzahl der Objekte [800]" +Chr$(10)
	For i = 0 To 799
		ownerState = ReadByte(index) Xor $FF
		AddTextAreaText area, "Obj["+i+"]  owner["+(ownerState And $0F)+"]  state["+(ownerState Shr 4)+"]" +Chr$(10)
		;                                  owner=Nibble 2                   state=Nibble 1
	Next

	AddTextAreaText area, Chr$(10)+ "Anzahl der Räume [55]" +Chr$(10)
	For i = 0 To 54
		file[i] = ReadByte(index): If file[i] Then file[i] = (file[i] Xor $FF) And $0F ;ASCII
	Next
	For i = 0 To 54
		off = ReadShort(index): If off Then off = off Xor $FFFF ;C64 Daten
		AddTextAreaText area, "Raum["+i+"]  Disk["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next

	AddTextAreaText area, Chr$(10)+ "Anzahl der Kostüme [35]" +Chr$(10)
	For i = 0 To 34
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To 34
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Kostüm["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next
	
	AddTextAreaText area, Chr$(10)+ "Anzahl der Skripte [200]" +Chr$(10)
	For i = 0 To 199
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To 199
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Skript["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next

	AddTextAreaText area, Chr$(10)+ "Anzahl der Sounds [100]" +Chr$(10)
	For i = 0 To 99
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To 99
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Sound["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next
CloseFile index

Repeat Until WaitEvent() = $803