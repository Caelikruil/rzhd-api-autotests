package ru.digital.services.sp.Utils;

import io.restassured.response.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.digital.services.sp.API.EmployeeApi;
import ru.digital.services.sp.Users.FamilyMember;
import ru.digital.services.sp.Users.User;

import static ru.digital.services.sp.Utils.XMLUtils.DocumentToString;
import static ru.digital.services.sp.Utils.XMLUtils.StringToDocument;

public class EmployeeUtils {

    public static boolean addFamilyMemberForEmployee(User user, FamilyMember member) throws Exception {
        Response responsePackage = EmployeeApi.getUserPackage(user.getSnilsXmlFileName());
        Document doc = StringToDocument(responsePackage.body().asString());
        Element memberElement = member.generateFamilyMemberNode(doc);

        Node rootNode = doc.getFirstChild();
        rootNode.appendChild(memberElement);

        EmployeeApi.changeUserPackage(user.getSnilsXmlFileName(), DocumentToString(doc));

        return isFamilyMemberExist(user, member.SerNum);
    }

    public static void removeFamilyMemberBySerNum(User user, String serNum) throws Exception {
        Response responsePackage = EmployeeApi.getUserPackage(user.getSnilsXmlFileName());
        Document doc = StringToDocument(responsePackage.body().asString());
        Node rootNode = doc.getFirstChild();
        Node familyMember = GetFamilyMemberNode(rootNode, serNum);
        if (familyMember == null)
            return;

        rootNode.removeChild(familyMember);
        EmployeeApi.changeUserPackage(user.getSnilsXmlFileName(), DocumentToString(doc));
    }

    public static boolean isFamilyMemberExist(User user, String serNum) throws Exception {
        Response responsePackage = EmployeeApi.getUserPackage(user.getSnilsXmlFileName());
        Document doc = StringToDocument(responsePackage.body().asString());
        Node rootNode = doc.getFirstChild();

        return GetFamilyMemberNode(rootNode, serNum) != null;
    }

    private static Node GetFamilyMemberNode(Node rootNode, String serNum){
        NodeList childNodes = rootNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeName() == "FamilyMember") {
                Node famMember = childNodes.item(i);
                NodeList famNodes = famMember.getChildNodes();
                for (int j = 0; j < famNodes.getLength(); j++) {
                    if (famNodes.item(j).getNodeName().equals("SerNum")
                            && famNodes.item(j).getTextContent().equals(serNum)) {
                        return famMember;
                    }
                }
            }
        }
        return null;
    }
}
