
            # RunAndFun

RunAndFun to aplikacja mobilna przeznaczona dla biegaczy, która umożliwia śledzenie biegu, mierzenie dystansu, tempa oraz czasu. Użytkownicy mogą przeglądać historię swoich biegów oraz zarządzać profilem.

## Funkcjonalności

- **Śledzenie biegu:** aplikacja pozwala na rozpoczęcie i zatrzymanie biegu, mierząc czas, dystans i tempo biegu w czasie rzeczywistym.
- **Powiadomienia push:** Otrzymywanie powiadomień co każdy przebiegnięty kilometr.
- **Historia biegów:** użytkownicy mogą przeglądać wyniki poprzednich biegów, w tym dystans, czas oraz tempo.
- **Profil użytkownika:** wyświetlanie informacji o użytkowniku oraz podsumowanie całkowitego dystansu, czasu i średniego tempa.
- **Mapy Google:** aplikacja wyświetla mapę, na której rysowana jest trasa biegu.

## Technologie

Aplikacja została zbudowana z użyciem następujących technologii:

- **Kotlin** – główny język programowania.
- **Android SDK (API 24+)** – minimalna wersja SDK.
- **Google Maps API** – do wyświetlania mapy i śledzenia trasy biegu.
- **MVVM (Model-View-ViewModel)** – architektura aplikacji.
- **Kotlin Coroutines** – do obsługi asynchroniczności.
- **LiveData** – do obserwacji danych w czasie rzeczywistym.

## Instalacja

1. **Sklonuj repozytorium:**
    ```bash
    git clone https://github.com/JakubPep/RunAndFun.git
    ```

2. **Otwórz projekt w Android Studio.**

3. **Dodaj klucz API Google Maps:**
   - Przejdź do pliku `AndroinManifest.xml` i dodaj swój klucz API w odpowiednie miejsce:
     ```xml
     <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="YOUR_API_KEY" />
     ```

4. **Uruchom aplikację:**
   - W Android Studio wybierz urządzenie emulatora lub fizyczne urządzenie i kliknij "Run".

## Struktura projektu

- `MainActivity.kt` – główny ekran aplikacji, obsługuje rozpoczęcie i zatrzymanie biegu oraz wyświetla mapę.
- `ProfileActivity.kt` – ekran profilu użytkownika z podsumowaniem biegów.
- `SummaryActivity.kt` – ekran podsumowujący zakończony bieg.
- `HistoryActivity.kt` – ekran wyświetlający historię biegów.
- `viewmodel/RunViewModel.kt` – logika aplikacji, zarządzanie czasem biegu i aktualizacjami danych.
- `res/layout/` – pliki XML definiujące układ interfejsu użytkownika.

## Testowanie

Aby uruchomić testy jednostkowe, wykonaj następujące kroki:

1. **Uruchom testy w Android Studio:**
   - Przejdź do sekcji "Run" i wybierz "Run Tests".

## Autorzy

**Szymon Sabatowski oraz Jakub Pepliński**  
Aplikacja stworzona w ramach projektu z przedmiotu Języki programowania urzadzeń mobilnych.
