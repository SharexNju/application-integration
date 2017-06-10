<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <stockComments xmlns="http://118.89.171.93/"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="http://118.89.171.93/ http://118.89.171.93/stockcomments.xsd">
            <stockCode>
                <xsl:value-of select="all/stockCode"/>
            </stockCode>
            <comments>

                <xsl:for-each select="all/single">
                    <comment>
                        <title>
                            <xsl:value-of select="title"/>
                        </title>
                        <url>
                            <xsl:value-of select="url"/>
                        </url>
                        <source>东方财富网</source>
                        <date>
                            <xsl:value-of select="date"/>
                        </date>
                    </comment>
                </xsl:for-each>
            </comments>
        </stockComments>
    </xsl:template>

</xsl:stylesheet>