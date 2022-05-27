package ru.digital.services.sp.Examples;

import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import ru.digital.services.sp.API.EmployeeApi;
import ru.digital.services.sp.API.PersonalDataRequests.PersonalDataRequests;
import ru.digital.services.sp.Dictionaries.AdminRoles;
import ru.digital.services.sp.Users.FamilyMember;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.StringReader;

import static ru.digital.services.sp.Utils.XMLUtils.DocumentToString;
import static ru.digital.services.sp.Utils.XMLUtils.StringToDocument;

public class WorkWithUsersExamples {

    @Test(testName = "Получение юзеров с монги")
    public void getUsers() {
        //Чтобы получить юзера по снилсу - просто указываем ожидаемый снилс и получаем объект User
        User userBySnils = Users.getUser("11691002627");

        //Если нам не принципиален конкретный юзер - мы можем получить любого по роли, например
        //метод вернет Администратора Событий
        User userByRole = Users.getUserByRole(AdminRoles.adminEvent);

        //Чтобы получить юзера, сначала нужно его положить в общую монгу на деве в базу autotests,
        //коллекцию users

        //Если юзера нет в коллекции, просто используйте фейковый метод, чтобы пользоваться классом
        // только с логином или паролем
        User user = Users.getFakeUser("02022035663", "Test2000");
    }

    @Test(testName = "Редактирование xml пакетов пользователей")
    public void changeUsers() throws Exception {
        User user = Users.getUser("02022035663");
        //Получим пакет юзера из файловой системы
        Response result = EmployeeApi.getUserPackage(user.getSnilsXmlFileName());

        //Приведем строку к XML документу
        Document doc = StringToDocument(result.body().asString());

        //Получим корень документа в нашем случае это ns2:v5
        Node rootNode = doc.getFirstChild();
        //Получим список нод внутри корневого тега - general, orgassigment и так далее
        NodeList childNodes = rootNode.getChildNodes();
        Node general = null;

        //Найдем в списке ноду с именем General
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeName() == "General") {
                general = childNodes.item(i);
                break;
            }
        }

        //Получим ноды в блоке General и для ноды FirstName отредактируем ее содержимое на строку "Новое имя"
        NodeList generalChilds = general.getChildNodes();
        for (int i = 0; i < generalChilds.getLength(); i++) {
            if (generalChilds.item(i).getNodeName() == "FirstName") {
                generalChilds.item(i).setTextContent("Новое имя");
                break;
            }
        }

        //Приведем содержимое XML документа обратно к строке
        String newXml = DocumentToString(doc);

        //Изменим содержимое пакета
        EmployeeApi.changeUserPackage(user.getSnilsXmlFileName(), newXml);

        //Повторим все то же самое и посмотрим, в каком значении сейчас тег FirstName
        Response result2 = EmployeeApi.getUserPackage(user.getSnilsXmlFileName());

        Document doc2 = StringToDocument(result2.body().asString());

        Node rootNode2 = doc2.getFirstChild();
        NodeList childNodes2 = rootNode2.getChildNodes();
        Node general2 = null;

        for (int i = 0; i < childNodes2.getLength(); i++) {
            if (childNodes2.item(i).getNodeName() == "General") {
                general2 = childNodes2.item(i);
                break;
            }
        }

        NodeList generalChilds2 = general2.getChildNodes();
        for (int i = 0; i < generalChilds2.getLength(); i++) {
            if (generalChilds.item(i).getNodeName() == "FirstName") {
                Assert.assertEquals(generalChilds2.item(i).getTextContent(), "Новое имя");
                break;
            }
        }

        //Подождем пока сервис отправит данные в другие сервисы
        //ToDo поиграться с таймингами и убедиться что оно работает
        Thread.sleep(40000);

        //Проверим что изменение появилось в перс-дате
        Response response = PersonalDataRequests.getPersData(user);
        Assert.assertEquals(response.jsonPath().getString("general.firstName"), "Новое имя");

        //Вернем все как было
        EmployeeApi.changeUserPackage(user.getSnilsXmlFileName(), result.body().asString());
    }

    @Test(testName = "Добавление ноды пользователю")
    public void addElementInPackage() throws Exception {
        User user = Users.getUser("02022035663");

        Response result = EmployeeApi.getUserPackage(user.getSnilsXmlFileName());
        Document doc = StringToDocument(result.body().asString());
        Node rootNode = doc.getFirstChild();
        NodeList childNodes = rootNode.getChildNodes();

        /*Проверим на наличие ноды в объекте, и если ее нет, добавим
        Добавлять будем объект в корень пакета
        <Test>
            <TestValue>Vasya</TestValue>
        </Test>
         */

        //Найдем в списке ноду с именем Test
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeName() == "Test") {
                //Удалим элемент
            }
        }

        //Создадим новый экземпляр элемента для вставки
        //Создадим основу для нашего нового объекта с именем Тест
        Element testElement = doc.createElement("Test");
        //Затем создадим новый элемент ТестВалю
        Element testValueElement = doc.createElement("TestValue");
        //В элементе тестВалю укажем, что его текстовое содержимое - Вася
        testValueElement.setTextContent("Vasya");
        //Положим все элементы к своим родителям, тест в корень, а тестВалю в тест
        testElement.appendChild(testValueElement);
        rootNode.appendChild(testElement);

        //Упакуем в строку и отправим в сервис
        String newXml = DocumentToString(doc);
        EmployeeApi.changeUserPackage(user.getSnilsXmlFileName(), newXml);

        //Проверим наш ответ на наличие Тест
        String xml2 = EmployeeApi.getUserPackage(user.getSnilsXmlFileName()).body().asString();
        Document doc2 = StringToDocument(xml2);
        Node rootNode2 = doc2.getFirstChild();
        NodeList childNodes2 = rootNode2.getChildNodes();

        Node removeChild = null;
        //Найдем в списке ноду с именем Test и посмотрим что внутри
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes2.item(i).getNodeName() == "Test") {
                removeChild = childNodes2.item(i);
                System.out.println("Успех, а внутри " + childNodes2.item(i).getChildNodes().item(0).getTextContent());
            }
        }

        //Удалим ненужный элемент
        rootNode2.removeChild(removeChild);
        System.out.println(DocumentToString(doc2));

        //Вернем все как было
        EmployeeApi.changeUserPackage(user.getSnilsXmlFileName(), result.body().asString());
    }

    @Test
    public void generateXmlChild() throws Exception {
        FamilyMember member = new FamilyMember("fass", "uaz");
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Element result = member.generateFamilyMemberNode(doc);
        doc.appendChild(result);
        System.out.println(DocumentToString(doc));
    }
}
