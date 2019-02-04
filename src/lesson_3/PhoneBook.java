package lesson_3;

/*
2. Написать простой класс PhoneBook(внутри использовать HashMap):
  - В качестве ключа использовать фамилию
  - В каждой записи всего два поля: phone, e-mail
  - Отдельный метод для поиска номера телефона по фамилии (ввели фамилию, получили ArrayList телефонов),
  и отдельный метод для поиска e-mail по фамилии. Следует учесть, что под одной фамилией может быть несколько записей.
  Итого должно получиться 3 класса Main, PhoneBook, Person.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhoneBook {
    private HashMap<String, ArrayList<Person>> phoneBook;

    PhoneBook() {
        phoneBook = new HashMap<>();

    }

    public void add (String surname, Person person) {
        ArrayList<Person> list = new ArrayList<>();
        ArrayList<Person> temp;
        list.add(person);
        if (!(phoneBook.containsKey(surname))) {
            this.phoneBook.put(surname, list);
        } else {
            temp = phoneBook.get(surname);
            temp.add(person);
            this.phoneBook.put(surname, temp);
        }
    }

    public String get (String surname) {
        String temp = null;
        for (Map.Entry<String, ArrayList<Person>> o : phoneBook.entrySet()) {
            if (o.getKey().equals(surname)) {
                temp = o.getValue().toString();
            }
        }
        return "Номер телефона <" + surname + "> - " + temp;
    }
}
