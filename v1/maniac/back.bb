win = CreateWindow("MM v1 Hintergrund", 150, 100, 350, 300, 0, 1)
button = CreateButton("Raum...", 10, 10, 65, 25, win)
panel = CreatePanel(10, 45, 320, 215, win)
can = CreateCanvas(0, 0, 1600, 200, panel): SetBuffer CanvasBuffer(can)
slider = CreateSlider(0, 200, 320, 15, panel): SetSliderRange slider, 320, 1600
char = CreateImage(8, 8, 256) ;Hintergründe nach C64 Standard in 8x8 Pixel Zeichen aufgeteilt
ClsColor 240, 240, 240: Cls
Dim col(3, 1), pal(15), ram(3399)

Data $FF000000, $FFFFFFFF, $FFAA0000, $FF00AAAA, $FFAA00AA, $FF00AA00, $FF0000AA, $FFFFFF55
Data $FFFF5555, $FFAA5500, $FFFF5555, $FF555555, $FFAAAAAA, $FF55FF55, $FF5555FF, $FFAAAAAA
;^EGA zu C64 Farbpalette
For i = 0 To 15: Read pal(i): Next

Repeat
	Select WaitEvent() 
		Case $401
			Select EventSource()
				Case button Gosub go
				Case slider SetGadgetShape can, -SliderValue(slider), 0, 1600, 200
			End Select
		Case $803 End
	End Select
Forever

.go
	file$ = RequestFile("00 ist keine Raumdatei!", "LFL")
	If file$ = "" Then Return
	Cls

	room = ReadFile(file$)
		SeekFile room, 4
		width = (ReadByte(room) Xor $FF) * 8: height = (ReadByte(room) Xor $FF) * 8
		For i = 0 To 3 ;Pro Hintergrund 3 feststehende und 1 freie Farbe für jedes Zeichen
			col(i, 0) = (ReadByte(room) Xor $FF) And $0F
		Next
		If col(3, 0) = 0 Then col(3, 0) = 1

		Gosub alone: Gosub frame: Gosub order
	CloseFile room
Return

.frame
	SeekFile room, 10: SeekFile(room, ReadShort(room) Xor $FFFF)
	For i = 0 To 3 ;Wiederkehrende Angaben
		col(i, 1) = ReadByte(room) Xor $FF
	Next

	set = 0
	Repeat
		run = ReadByte(room) Xor $FF
		If run < $40
			For i = 0 To run
				px = ReadByte(room) Xor $FF
				For j = 6 To 0 Step - 2
					For z = 0 To 1
						WritePixel xpos, ypos, pal(col((px Shr j) And %11, 0)), ImageBuffer(char, set)
						xpos = xpos + 1
					Next
				Next
				xpos = 0: ypos = ypos + 1
				If ypos = 8 Then ypos = 0: set = set + 1
			Next
		ElseIf run < $80
			run = run And %00111111
			px = ReadByte(room) Xor $FF
			For i = 0 To run
				For j = 6 To 0 Step - 2
					For z = 0 To 1
						WritePixel xpos, ypos, pal(col((px Shr j) And %11, 0)), ImageBuffer(char, set)
						xpos = xpos + 1
					Next
				Next
				xpos = 0: ypos = ypos + 1
				If ypos = 8 Then ypos = 0: set = set + 1
			Next
		Else
			px = col((run Shr 5) And %11, 1)
			run = run And %00011111
			For i = 0 To run
				For j = 6 To 0 Step - 2
					For z = 0 To 1
						WritePixel xpos, ypos, pal(col((px Shr j) And %11, 0)), ImageBuffer(char, set)
						xpos = xpos + 1
					Next
				Next
				xpos = 0: ypos = ypos + 1
				If ypos = 8 Then ypos = 0: set = set + 1
			Next
		EndIf
	Until set = 256 ;Nach C64 Standard 256 Zeichen pro Hintergrund 
Return

.order
	For i = 0 To 3
		col(i, 1) = ReadByte(room) Xor $FF
	Next

	xpos = 0: px = (width / 8) * (height / 8) - 1
	Repeat
		run = ReadByte(room) Xor $FF
		If run < $40
			For i = 0 To run
				set = ReadByte(room) Xor $FF
				DrawBlock char, xpos, ypos, set
				If ram(px) <> col(3, 0) ;Farbe 4 erstetzen
					For j = 0 To 7
						For k = 0 To 6 Step 2
							If ReadPixel(k + xpos, j + ypos) = pal(col(3, 0))
								For l = 0 To 1
									WritePixel k + l + xpos, j + ypos, pal(ram(px))
								Next
							EndIf
						Next
					Next
				EndIf
				ypos = ypos + 8: px = px - 1
				If ypos = height Then ypos = 0: xpos = xpos + 8
			Next
		ElseIf run < $80
			run = run And %00111111
			set = ReadByte(room) Xor $FF
			For i = 0 To run
				DrawBlock char, xpos, ypos, set
				If ram(px) <> col(3, 0)
					For j = 0 To 7
						For k = 0 To 6 Step 2
							If ReadPixel(k + xpos, j + ypos) = pal(col(3, 0))
								For l = 0 To 1
									WritePixel k + l + xpos, j + ypos, pal(ram(px))
								Next
							EndIf
						Next
					Next
				EndIf
				ypos = ypos + 8: px = px - 1
				If ypos = height Then ypos = 0: xpos = xpos + 8
			Next
		Else
			set = col((run Shr 5) And %11, 1)
			run = run And %00011111
			For i = 0 To run
				DrawBlock char, xpos, ypos, set
				If ram(px) <> col(3, 0)
					For j = 0 To 7
						For k = 0 To 6 Step 2
							If ReadPixel(k + xpos, j + ypos) = pal(col(3, 0))
								For l = 0 To 1
									WritePixel k + l + xpos, j + ypos, pal(ram(px))
								Next
							EndIf
						Next
					Next
				EndIf
				ypos = ypos + 8: px = px - 1
				If ypos = height Then ypos = 0: xpos = xpos + 8
			Next
		EndIf
	Until xpos = width
	
	FlipCanvas(can)
Return

.alone
	SeekFile room, 14: SeekFile(room, ReadShort(room) Xor $FFFF)
	For i = 0 To 3
		col(i, 1) = (ReadByte(room) Xor $FF) And %00000111 ;Bit 3 C64 Daten
	Next

	set = (width / 8) * (height / 8) - 1
	Repeat
		run = ReadByte(room) Xor $FF
		If run < $40
			For i = 0 To run
				ram(set) = (ReadByte(room) Xor $FF) And %00000111
				set = set - 1
			Next
		ElseIf run < $80
			run = run And %00111111
			rep = (ReadByte(room) Xor $FF) And %00000111
			For i = 0 To run
				ram(set) = rep
				set = set - 1
			Next
		Else
			rep = col((run Shr 5) And %11, 1)
			run = run And %00011111
			For i = 0 To run
				ram(set) = rep
				set = set - 1
			Next
		EndIf
	Until set = -1
Return