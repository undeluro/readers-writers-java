# Problem Czytelników i Pisarzy

## Opis algorytmu

Zaimplementowany algorytm rozwiązuje klasyczny problem współbieżności "Czytelnicy i Pisarze" z następującymi ograniczeniami:
1. Maksymalnie 5 czytelników może przebywać w czytelni jednocześnie.
2. Pisarz ma wyłączny dostęp do czytelni (w danej chwili może być tylko 1 pisarz i 0 czytelników).
3. Brak zagłodzenia - każdy wątek chcący wejść do czytelni ma gwarancję, że w końcu do niej wejdzie.
4. Uczciwość (Fairness) - wątki są wpuszczane zgodnie z kolejnością zgłoszenia (FCFS - First-Come, First-Served).

### Implementacja
Algorytm wykorzystuje jeden `java.util.concurrent.Semaphore` zainicjalizowaną z flagą `fair = true`.
- Semafor posiada 5 pozwoleń (permits).
- Czytelnik, aby wejść, pobiera 1 pozwolenie (`semaphore.acquire(1)`).
- Pisarz, aby wejść, musi pobrać wszystkie 5 pozwoleń (`semaphore.acquire(5)`), co gwarantuje mu wyłączność.
- Dzięki flagi `fair = true` w semaforze, Java zarządza wewnętrzną kolejką wątków w sposób sprawiedliwy, co zapobiega zagłodzeniu i realizuje zasadę FCFS.

## Sposób uruchomienia

Program można zbudować i uruchomić przy użyciu Maven:

1. Budowanie paczki JAR:
   ```bash
   mvn clean package
   ```

2. Uruchomienie programu:
   ```bash
   java -jar target/reader-writer-project-1.0-SNAPSHOT.jar [liczbaCzytelników] [liczbaPisarzy] [czasUśpienia]
   ```
   Domyślne wartości (jeśli nie podano parametrów):
   - Liczba czytelników: 10
   - Liczba pisarzy: 3
   - Czas uśpienia między operacjami: 500 ms

Przykład:
```bash
java -jar target/reader-writer-project-1.0-SNAPSHOT.jar 100 5
```

## Testy i Pokrycie
Projekt zawiera testy jednostkowe JUnit 5, które osiągają 100% pokrycia kodu (poza metodą main w klasie App).
Aby uruchomić testy i sprawdzić pokrycie:
```bash
mvn verify
```
Raport pokrycia JaCoCo znajduje się w `target/site/jacoco/index.html`.
Raporty SonarQube w `sonarqube/`.

## Dokumentacja Javadoc
Dokumentacja klas została wygenerowana w katalogu `javadoc`. Można ją również wygenerować poleceniem:
```bash
mvn javadoc:javadoc
```
