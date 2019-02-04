package lesson_3;

/*

 2. Написать простой класс PhoneBook(внутри использовать HashMap):
  - В качестве ключа использовать фамилию
  - В каждой записи всего два поля: phone, e-mail
  - Отдельный метод для поиска номера телефона по фамилии (ввели фамилию, получили ArrayList телефонов),
  и отдельный метод для поиска e-mail по фамилии. Следует учесть, что под одной фамилией может быть несколько записей.
  Итого должно получиться 3 класса Main, PhoneBook, Person.
 */

import java.util.*;

public class HomeWorkCollection {
/*
1. Создать массив с набором слов (20-30 слов, должны встречаться повторяющиеся):
  - Найти список слов, из которых состоит текст (дубликаты не считать);
  - Посчитать сколько раз встречается каждое слово (использовать HashMap);
 */
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList("Иванов", "Сидоров", "Иванов", "Федоров", "Иванов",
                "Петров", "Сидоров", "Федоров", "Иванов", "Тимофеев", "Тихонов", "Сидоров", "Иванов", "Иванов", "Петров",
                "Тихонов", "Иванов", "Сидоров", "Петров", "Тимошенко", "Иванов", "Игнатов", "Иванов", "Петров"));
        LinkedHashSet<String> set = new LinkedHashSet<>(list);

        System.out.println(list);
        System.out.println(set);

        int count;
        String word;
        HashMap<String, Integer> map = new HashMap<>();

        Iterator<String> iterator = set.iterator();

        for (int i = 0; i < list.size(); i++) {
            while (iterator.hasNext()) {
                word = iterator.next();
                count = Collections.frequency(list, word);
                map.put(word, count);
            }
        }

        for (Map.Entry<String, Integer> o : map.entrySet()) {
            System.out.println("Элемент: " + o.getKey() + " - " + o.getValue());
        }



        //Задание 2.
        PhoneBook phoneBook = new PhoneBook();
        phoneBook.add("Иванов", new Person(123, "123"));
        phoneBook.add("Григорьев", new Person(234, "234"));
        phoneBook.add("Петров", new Person(345, "345"));
        phoneBook.add("Сидоров", new Person(456, "456"));
        phoneBook.add("Тимофеев", new Person(567, "567"));
        phoneBook.add("Григорьев", new Person(678, "678"));
        phoneBook.add("Мартиросян", new Person(789, "789"));
        phoneBook.add("Воля", new Person(891, "891"));
        phoneBook.add("Харламов", new Person(912, "912"));

        System.out.println(phoneBook.get("Григорьев"));
    }
}
