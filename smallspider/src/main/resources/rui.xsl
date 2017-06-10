<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <stockComments xmlns="http://118.89.171.93/"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="http://118.89.171.93/ http://118.89.171.93/stockcomments.xsd">
            <stockCode>
                <xsl:value-of select="stockinfo/stock-code"/>
            </stockCode>
            <comments>

                <xsl:for-each select="stockinfo/one-post">
                    <comment>
                        <title>
                            <xsl:value-of select="title"/>
                        </title>
                        <url>
                            <xsl:value-of select="href"/>
                        </url>
                        <source>论股堂</source>
                        <date>
                            <xsl:value-of select="time"/>
                        </date>
                    </comment>
                </xsl:for-each>
            </comments>
        </stockComments>
    </xsl:template>

</xsl:stylesheet>