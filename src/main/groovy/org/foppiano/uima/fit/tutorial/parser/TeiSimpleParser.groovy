package org.foppiano.uima.fit.tutorial.parser

/**
 * Created with IntelliJ IDEA.
 * User: lf84914
 * Date: 1/16/14
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
class TeiSimpleParser {

    String parse(String xml) {
        Node rootNode = new XmlParser().parseText(xml)

        NodeList textNode = rootNode.'teiCorpus'?.'TEI'?.'text'?.'group'?.'text'[0]?.'body'?.'div'

        Node claimsNode
        Node descriptionNode

        textNode?.each {
            if (it?.attributes()?.containsValue('claims')) {
                claimsNode = it
            }
            if (it?.attributes()?.containsValue('description')) {
                descriptionNode = it
            }
        }

        String claims = claimsNode == null ? "" : claimsNode.'**'.'p'.collect{ it.text() }.join("\n")
        String description = descriptionNode == null ? "" : descriptionNode.'**'.'p'.collect{ it.text() }.join("\n")

        return "CLAIMS\n ${claims} \n\n DESCRIPTION \n ${description}"

    }
}
