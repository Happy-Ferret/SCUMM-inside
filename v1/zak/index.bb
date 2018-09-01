AppTitle "Zak v1 Index"
win = CreateWindow("Zak v1 Index", 200, 100, 230, 360, 0, 1) 
area = CreateTextArea(10, 10, 200, 310, win)
Local file[154]

index = ReadFile("00.LFL")
	If Not index Then RuntimeError "Datei nicht gefunden!"

	ReadShort(index) ;C64 Daten
	AddTextAreaText area, "Anzahl der Objekte [775]" +Chr$(10)
	For i = 0 To 774
		ownerState = ReadByte(index) Xor $FF
		AddTextAreaText area, "Obj["+i+"]  owner["+(ownerState And $0F)+"]  state["+(ownerState Shr 4)+"]" +Chr$(10)
		;                                  owner=Nibble 2                   state=Nibble 1
	Next

	AddTextAreaText area, Chr$(10)+ "Anzahl der Räume [61]" +Chr$(10)
	For i = 0 To 60
		file[i] = ReadByte(index): If file[i] Then file[i] = (file[i] Xor $FF) And $0F ;ASCII
	Next
	For i = 0 To 60
		off = ReadShort(index): If off Then off = off Xor $FFFF ;C64 Daten
		AddTextAreaText area, "Raum["+i+"]  Disk["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next

	AddTextAreaText area, Chr$(10)+ "Anzahl der Kostüme [37]" +Chr$(10)
	For i = 0 To 36
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To 36
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Kostüm["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next
	
	AddTextAreaText area, Chr$(10)+ "Anzahl der Skripte [155]" +Chr$(10)
	For i = 0 To 154
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To 154
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Skript["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next

	AddTextAreaText area, Chr$(10)+ "Anzahl der Sounds [120]" +Chr$(10)
	For i = 0 To 119
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To 119
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Sound["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next
CloseFile index

Repeat Until WaitEvent() = $803