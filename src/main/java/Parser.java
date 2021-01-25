import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {
    public static void main(String[] args) throws Exception {
        Document page = getPage();  //запрашиваем страницу для парсинга. метод getPage() скачивает ее с сайта в виде объекта Document
        // используеи css query language с помощью которого можем брать данные со страницы
        int index = 0;
        // запрашиваем со страницы все tables, и в квадратных скобках указываем тип класса wt, который содердит нужную
        // нам информацию о дате. Таких элементов может быть много, поэтому просим отдать нам первый .first();
        Element tableWth = page.select("table[class=wt]").first();

        // System.out.println(tableWth); // мы можем вывести на экран эту таблицу tableWt в виде кода HTML
        // и увидеть, что наша таблица содержит имена столбцов wth и значения valign

        // далее забираем имена столбцов (столбцы в таблице это tr), имена столбцов  имеют аттрибут wth на странице сайта
        // то есть из нашей таблицы мы выбираем только те trы, чей класс равен wth
        Elements names = tableWth.select("tr[class=wth]");

        // теперь нам нужнополучить values.
        // то есть из нашей таблицы мы выбираем только те trы, чей valign равен top
        Elements values = tableWth.select("tr[valign=top]");

        // теперь выведем на экран таблицу со значениями погоды на каждую дату, на утро, день, вечер, ночь
        // для этого делаем цикл и пробегаемся по names
        for (Element name : names) {
            String dateString = name.select("th[id=dt]").text(); //записываем в переменную date все, что лежит в блоке th c id=dt
            String date = getDateFromString(dateString); // так как в переменной date содержится иная информация, нам нужно выдрать только дату, см. метод ниже
            System.out.println(date + "    Явления    Температура    Давление   Влажность    Ветер");
            int iterationCount = printPartValues(values, index);
            index += iterationCount;
        }
    }

    //************************** Получаем страницу с сайта в виде документа HTML**********************************
    public static Document getPage() throws IOException { //используем тип Document т.к. он поддерживает вложенность <div>
        String url = ("http://www.pogoda.spb.ru/");       // передаем адрес сайта, который будем парсить
        Document page = Jsoup.parse(new URL(url), 3000); //задаем время ожмдания ответа
        return page;                                        // возвращаем код страницы в виде HTML, котрую будем потом парсить
    }

    // pattern и matcher - механизм для того, тчобы искать нужную нам информацию в тексе
    // pattern - это шаблон, который мы ищем
    // matcher - ищет совпадения с паттерном
    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    // метод для получения даты из строки с использованием регулярного выражения
    private static String getDateFromString(String stringDate) throws Exception {

        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()) {  // если matcher нашел нужный результат, то говорим
            return matcher.group(); // верни нужный результат. Команда matcher.group();
        }
        throw new Exception("Can't extract date from string");
    }

    private static int printPartValues(Elements values, int index) {
        int iterationCount = 4;
        if (index == 0) {
            Element valueLn = values.get(0);
            /**  boolean isMorning = valueLn.text().contains("Утро");
             if (isMorning){
             iterationCount = 3;
             }*/

            switch (valueLn.text().split(" ")[0]) {
                case ("День"):
                    iterationCount = 3;
                    break;
                case ("Вечер"):
                    iterationCount = 2;
                    break;
                case ("Ночь"):
                    iterationCount = 1;
                    break;
            }

        }

        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);
            for (Element td : valueLine.select("td")) {
                System.out.print(td.text() + "    ");
            }
            System.out.println();
        }
        return (iterationCount);
    }
}