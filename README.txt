Made by : Krešimir Kuren, Karlo Rogina, Marin Nekiæ

Aplikacija ShopShop za Android napravljena je po uzoru na Aplikaciju ShopShop za Iphone. Aplikacije imaju iste funkcionalnosi i meðusobno su kompatibilne. Omoguæuje stvaranje/izmjenu lista, stvaranje/izmjenu stvari na listi, te sinkronizaciju sa Dropbox raèunom. Aplikacija ima mnogo prostora za poboljšanja.

Upute za instalaciju:

a)	Potrebno je preuzeti Android Bundle za odgovarajuæu platformu. (URL: http://developer.android.com/sdk/index.html)
	Android Bundle sadrži:
		- Eclipse razvojnu okolinu sa ADT plugin-om
		- Android SDK
		- Android Virtual Device (emulator na kojem je moguæe pokrenuti aplikaciju)
	Potrebno je raspakirati arhivu te pokrenuti razvojnu okolinu Eclipse.

b)	Potrebno je instalirati odgovarajuæe verzije SDK:
		1. U Eclipse-u odabrati Window -> Android SDK Manager
		2. Potrebno je oznaèiti Android 4.3 (API 18)
		3. Instalirati oznaèene pakete klikom na gumb Install
		
	Napomena: Ako aplikaciju želite pokrenuti na stvarnom ureðaju (Windows platforma), prilikom instalacije je potrebno
	odabrati Extras -> Google USB driver
	
c)	Sadržaj arhive AplikacijaShopShop.zip potrebno je importati u Eclipse:
		1. U Eclipse-u, potrebno je odabrati File -> import
		2. Iz izbornika je potrebno odabrati "Existing Projects into Workspace" i kliknuti next
		3. Oznaèiti "Select root directory" te kliknuti na Browse... te odabrati lokaciju ShopShop i HoloColorPicker-master foldera
		4. Oznaèiti projekte HoloColorPicker-master i ShopShop
		5. Klikom na Finish, projekti æe biti importani u Eclipse
	
	Napomena: Ponekad prilikom importa projekta u Eclipse nastanu greške. U tom sluèaju potrebno je napraviti sljedeæe:
		1. Desni klik na ShopShop -> Close project
		2. Dvostruki klik na ShopShop èime se projekt ponovo otvara
		i/ili
		1. Desni klik na ShopShop -> Android tools -> Fix Project Properties
	
d)	Potrebno je kreirati novi Virtualni ureðaj (AVD):
		1. U Eclipse-u odabrati Window -> Android Virtual Device Manager -> New
		2. Podesiti postavke i kliknuti na OK èime se stvara novi virtualni ureðaj
		
	Napomena: Alternativno, moguæe je preko USB kabla spojiti na raèunalo stvarni ureðaj,
	ako je instaliran Google USB driver iz koraka b)
	
	Napomena2: U nekim sluèajevima AVD-u je potrebno mnogo vremena za pokretanje i radi vrlo sporo. Aplikaciju je
	takoðer moguæe pokrenuti na Genymotion emulatoru. Za instalaciju Genymotiona-a, potrebno se besplatno registrirati
	na stranici http://www.genymotion.com/ te slijediti upute za instalaciju.
	
e)	Aplikaciju je potrebno instalirati na ureðaj/emulator:
	1. Desni klik na ShopShop -> Run as -> Android application
	2. Eclipse nudi popis ureðaja/emulatora na koje je moguæe instalirati aplikaciju
	3. Potrebno je odabrati jedan ureðaj/emulator sa popisa
	4. Aplikacija se nakon toga instalira i pokreæe na emulatoru/ureðaju
	
