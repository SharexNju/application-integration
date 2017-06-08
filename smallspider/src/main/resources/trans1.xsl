<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <comments xmlns="http://118.89.171.93/"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xsi:schemaLocation="http://118.89.171.93/ http://118.89.171.93/stockcomments.xsd">
            <xsl:for-each select="starts/start">
                <comment>
                    <title>
                        <xsl:value-of select="a"/>
                    </title>
                    <url>
                        <xsl:value-of select="b"/>
                    </url>
                    <source>
                        <xsl:value-of select="c"/>
                    </source>
                    <date>
                        <xsl:value-of select="d"/>
                    </date>
                    <stockCode>
                        <xsl:value-of select="e"/>
                    </stockCode>
                </comment>
            </xsl:for-each>

        </comments>
    </xsl:template>

</xsl:stylesheet>