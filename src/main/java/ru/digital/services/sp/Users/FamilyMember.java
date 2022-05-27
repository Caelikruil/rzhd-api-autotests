package ru.digital.services.sp.Users;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.digital.services.sp.Utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FamilyMember {
    public String Famsa;
    public String FamsaName;
    public String LastNameF;
    public String FirstNameF;
    public String MidNameF;
    public String SexF;
    public String BirthDateF;
    public String SerNum;
    public String Natio;
    public String DocType;
    public String DocSer;
    public String DocNum;
    public String DocDate;

    public FamilyMember(String famsa, String serNum) {
        Famsa = famsa;
        SerNum = serNum;
        FamsaName = Utils.randomRussianWords(1);

        BirthDateF = LocalDate.now().minusYears(8).format(DateTimeFormatter.ISO_DATE);
        Natio = Utils.randomRussianWords(1);

        DocDate = LocalDate.now().minusMonths(8).format(DateTimeFormatter.ISO_DATE);
        DocNum = Utils.randomRussianWords(1);
        DocSer = Utils.randomRussianWords(1);

        LastNameF = Utils.randomRussianWords(1);
        MidNameF = Utils.randomRussianWords(1);
        FirstNameF = Utils.randomRussianWords(1);
        SexF = "Ж";
    }

    public Element generateFamilyMemberNode(Document doc) {
        Element familyMember = doc.createElement("FamilyMember");

        Class<?> enclosingClass = getClass();
        for (Method method : enclosingClass.getDeclaredMethods()
        ) {
            if (method.getName().startsWith("get")) {
                try {
                    Element el = doc.createElement(method.getName().substring(3));
                    Object result = method.invoke(this);
                    el.setTextContent((String) result);
                    familyMember.appendChild(el);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return familyMember;
    }

    public String getFamsa() {
        return Famsa;
    }

    public String getFamsaName() {
        return FamsaName;
    }

    public String getLastNameF() {
        return LastNameF;
    }

    public String getFirstNameF() {
        return FirstNameF;
    }

    public String getMidNameF() {
        return MidNameF;
    }

    public String getSexF() {
        return SexF;
    }

    public String getBirthDateF() {
        return BirthDateF;
    }

    public String getSerNum() {
        return SerNum;
    }

    public String getNatio() {
        return Natio;
    }

    public String getDocType() {
        return DocType;
    }

    public String getDocSer() {
        return DocSer;
    }

    public String getDocNum() {
        return DocNum;
    }

    public String getDocDate() {
        return DocDate;
    }
}

/*
 <FamilyMember>
        <Famsa>2</Famsa>
        <FamsaName>Ребенок</FamsaName>
        <LastNameF>Сидорова</LastNameF>
        <FirstNameF>Анастасия</FirstNameF>
        <MidNameF>Кирилловна</MidNameF>
        <SexF>Ж</SexF>
        <BirthDateF>2006-06-06</BirthDateF>
        <SerNum>15</SerNum>
        <Natio>RU</Natio>
        <DocType>1</DocType>
        <DocSer>VII-МЮ</DocSer>
        <DocNum>501055</DocNum>
        <DocDate>2019-05-18</DocDate>
    </FamilyMember>
 */