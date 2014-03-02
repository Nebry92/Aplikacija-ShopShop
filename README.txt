Made by : Kre�imir Kuren, Karlo Rogina, Marin Neki�

Aplikacija ShopShop za Android napravljena je po uzoru na Aplikaciju ShopShop za Iphone. Aplikacije imaju iste funkcionalnosi i me�usobno su kompatibilne. Omogu�uje stvaranje/izmjenu lista, stvaranje/izmjenu stvari na listi, te sinkronizaciju sa Dropbox ra�unom. Aplikacija ima mnogo prostora za pobolj�anja.

Upute za instalaciju:

a)	Potrebno je preuzeti Android Bundle za odgovaraju�u platformu. (URL: http://developer.android.com/sdk/index.html)
	Android Bundle sadr�i:
		- Eclipse razvojnu okolinu sa ADT plugin-om
		- Android SDK
		- Android Virtual Device (emulator na kojem je mogu�e pokrenuti aplikaciju)
	Potrebno je raspakirati arhivu te pokrenuti razvojnu okolinu Eclipse.

b)	Potrebno je instalirati odgovaraju�e verzije SDK:
		1. U Eclipse-u odabrati Window -> Android SDK Manager
		2. Potrebno je ozna�iti Android 4.3 (API 18)
		3. Instalirati ozna�ene pakete klikom na gumb Install
		
	Napomena: Ako aplikaciju �elite pokrenuti na stvarnom ure�aju (Windows platforma), prilikom instalacije je potrebno
	odabrati Extras -> Google USB driver
	
c)	Sadr�aj arhive AplikacijaShopShop.zip potrebno je importati u Eclipse:
		1. U Eclipse-u, potrebno je odabrati File -> import
		2. Iz izbornika je potrebno odabrati "Existing Projects into Workspace" i kliknuti next
		3. Ozna�iti "Select root directory" te kliknuti na Browse... te odabrati lokaciju ShopShop i HoloColorPicker-master foldera
		4. Ozna�iti projekte HoloColorPicker-master i ShopShop
		5. Klikom na Finish, projekti �e biti importani u Eclipse
	
	Napomena: Ponekad prilikom importa projekta u Eclipse nastanu gre�ke. U tom slu�aju potrebno je napraviti sljede�e:
		1. Desni klik na ShopShop -> Close project
		2. Dvostruki klik na ShopShop �ime se projekt ponovo otvara
		i/ili
		1. Desni klik na ShopShop -> Android tools -> Fix Project Properties
	
d)	Potrebno je kreirati novi Virtualni ure�aj (AVD):
		1. U Eclipse-u odabrati Window -> Android Virtual Device Manager -> New
		2. Podesiti postavke i kliknuti na OK �ime se stvara novi virtualni ure�aj
		
	Napomena: Alternativno, mogu�e je preko USB kabla spojiti na ra�unalo stvarni ure�aj,
	ako je instaliran Google USB driver iz koraka b)
	
	Napomena2: U nekim slu�ajevima AVD-u je potrebno mnogo vremena za pokretanje i radi vrlo sporo. Aplikaciju je
	tako�er mogu�e pokrenuti na Genymotion emulatoru. Za instalaciju Genymotiona-a, potrebno se besplatno registrirati
	na stranici http://www.genymotion.com/ te slijediti upute za instalaciju.
	
e)	Aplikaciju je potrebno instalirati na ure�aj/emulator:
	1. Desni klik na ShopShop -> Run as -> Android application
	2. Eclipse nudi popis ure�aja/emulatora na koje je mogu�e instalirati aplikaciju
	3. Potrebno je odabrati jedan ure�aj/emulator sa popisa
	4. Aplikacija se nakon toga instalira i pokre�e na emulatoru/ure�aju
	
