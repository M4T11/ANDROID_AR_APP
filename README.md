
# AR

Przeznaczeniem aplikacji jest wykrywanie i „identyfikacja” w czasie rzeczywistym - w oparciu
o zdefiniowaną bazę deskryptorów - obiektów znajdujących się na obrazie dostarczanym z wbudowanej
kamery urządzenia.

Główną funkcjonalnością aplikacji jest wyświetlenie użytkownikowi na ekranie smartfona - po
poprawnym wykryciu szukanego obiektu - krótkiej informacji o tym obiekcie w formie prostego
interfejsu graficznego.

W implementacji aplikacji wykorzystano istniejące algorytmy wykrywania i metody dopasowywania obiektów w obrazie na podstawie ich deskryptorów.


## Wymagania

- smartfon lub symulator smartfona z systemem operacyjnym Android w wersji 7.0 lub nowszej.


## Instalacja

Aplikacje można zainstalować na dwa sposoby:

- kompilacja & instalacja aplikacji poprzez Android Studio,
- pobranie pliku .apk (zakładka "Releases") i instalacja bezpośrednio na smartfonie.

    
## Sposób użycia

- Wybierz w widoku ustawień jeden z czterech dostępnych algorytmów algorytmów wykrywania i opisywania obiektów w obrazie (SIFT, SURF, FAST-SURF lub ORB) oraz jedną z dwóch dostępnych metod dopasowywania deskryptorów: Brute-Force lub metoda oparta na FLANN,
- Widok: "Rejestrowanie deskryptorów", pozwala na użycie narzędzia do rejestrowania deskryptorów wybranych obiektów w obrazie.  
    * Przed pierwszym użyciem do poprawnego działania aplikacji wymagane jest zarejestrowanie oraz zapisanie deskryptorów wybranego obiektu.  
    * Każdy z algorytmów posiada własną bazę deskryptorów obiektów, zatem wykrycie obiektu musi zostać wykonane z użyciem tego samego algorytmu, który został wykorzystany w procesie rejestrowania deskryptorów.
- Użyj jednego z trzech dostępnych trybów działania:

| TRYB  | FUNKCJONALNOŚĆ |
| ------------- |:-------------:|
| Tryb 1 - zaznacz obiekt     | Tryb 1. pozwala użytkownikowi wybrać na obrazie wyświetlanym na ekranie smartfona z kamery urządzenia, interesujący go obiekt. Następnie w oparciu o bazę zarejestrowanych deskryptorów - aplikacja próbuje dopasować deskryptory wybranego obiektu do deskryptorów przechowywanych w lokalnej bazie. Zwracany jest obiekt, który posiada najwięcej dopasowań. Jeżeli liczba znalezionych dopasowań przekroczy ustalony próg, obiekt taki uznawany jest jako dopasowany i na ekranie smartfona zostaje wyświetlone dodatkowe menu zawierające informacje na temat wybranego obiektu.     |
| Tryb 2 - pełna klatka kamery     | Aplikacja w trybie 2. działa na tej samej zasadzie jak w trybie 1. z tą różnicą, że użytkownik nie wybiera obiektu, a aplikacja umożliwia jedynie wyszukanie i dopasowanie obiektu na całym aktualnie widzianym obrazie z kamery urządzenia. Aplikacja zwróci informacje zawsze tylko o jednym obiekcie, który posiadać będzie największą liczbę dopasowań.     |
| Tryb 3 - tryb demonstracyjny      | Tryb 3. pozwala użytkowniki w czasie rzeczywistym obserwować na obrazie widzianym przez kamerę urządzenia, wizualne zobrazowanie procesu dopasowywania deskryptorów obiektu, wybranego z listy wcześniej zdefiniowanych obiektów (deskryptorów).     |

## Autor
- Mateusz GALAN

