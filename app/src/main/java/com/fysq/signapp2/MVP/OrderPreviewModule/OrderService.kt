package com.fysq.signapp2.MVP.OrderPreviewModule

import android.content.Context
import android.os.Environment
import android.util.Log
import com.fysq.signapp2.Base.PostBean.NorJson
import com.fysq.signapp2.Base.PostBean.Params
import com.fysq.signapp2.MyApp
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.PdfUtils
import com.fysq.signapp2.Utils.Utils
import com.google.gson.Gson
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.dom4j.io.SAXReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*




/**
 * Created by Miko on 2017/11/15.
 *
 */
class OrderService {
    private val TAG = "OrderPreviewService"


    private val FTITLE_SIZE = 18
    private val TTITLE_SIZE = 8
    private val STITLE_SIZE = 9

    internal fun getOrderMsg(orderNum: String, postCall: Utils.PostCall) {
        val norJson = NorJson()
        norJson.mac = Utils.getMac()
        norJson.account = Utils.getAccount()
        norJson.id = MyApp.context.getString(R.string.id_order_msg)
        val paramsList = ArrayList<Params>()
        val param1 = Params()
        param1.param1 = "条码"
        param1.param2 = orderNum

        paramsList.add(param1)

        norJson.param_list = paramsList

        val loginPostJson = Gson().toJson(norJson)

        Log.d(TAG, loginPostJson)

        Utils.normalQuery(norJson, postCall)

    }

    public fun parseOrderXml(xml: String, insertSign: Boolean): String {

        Log.i("xyz", xml);
        val reader = SAXReader()
        val ins = ByteArrayInputStream(xml.toByteArray())
        val document = reader.read(ins)

        val root = document.rootElement

        var header = root.elements()[0]
        val headElements = root.elements()[0].elements()
        val rectPageSize = Rectangle(PageSize.A4)
        val pdfDoc = Document(rectPageSize, 5f, 5f, 36f, 36f)
        pdfDoc.pageSize = rectPageSize.rotate()

        val dir = File(Environment
                .getExternalStorageDirectory().path + "/SignApp2")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val path = Environment
                .getExternalStorageDirectory().path + "/SignApp2/" + Utils.getAccount() + "_" + Date().time.toString() + ".pdf"
        val pdfFile = FileOutputStream(path)
        PdfWriter.getInstance(pdfDoc, pdfFile)

        pdfDoc.open()

        var supplier = header.element("供货商").text

//        出货单不同标题不同

        var titleStr = "北京大学试剂管理平台器材办送货单"


        if (supplier == "北京大学化学院器材办") {
            titleStr = "北京大学试剂管理平台器材办出库单"
        }

        //标题
        val p1 = PdfUtils.getBParagraph(FTITLE_SIZE, "\n\n$titleStr\n\n")
        p1.alignment = Element.ALIGN_CENTER
        pdfDoc.add(p1)

        //头部表格
        val table = PdfPTable(4)

        val orderId = header.element("出库单号").text

        val sp = MyApp.context.getSharedPreferences("order", Context.MODE_PRIVATE)
        sp.edit().putString("orderId", orderId).apply()

        if (supplier == "北京大学化学院器材办") {
            table.addCell(
                    createNoBorderCell("出库单号" + ":"
                            + header.element("出库单号").text))
        } else {
            table.addCell(
                    createNoBorderCell("送货单号" + ":"
                            + header.element("出库单号").text))
        }

        table.addCell(
                createNoBorderCell("院系" + ":"
                        + header.element("院系").text))
        table.addCell(
                createNoBorderCell("课题组" + ":"
                        + header.element("课题组").text))
        table.addCell(
                createNoBorderCell("出库日期" + ":"
                        + header.element("出库日期").text))

//        table.addCell(
//                createNoBorderCell("供货商" + ":"
//                        + header.element("供货商").text, 2))

        table.addCell(
                createNoBorderCell("订购人" + ":"
                        + header.element("订购人").text))
        table.addCell(
                createNoBorderCell("收货人" + ":"
                        + header.element("收货人").text))
        table.addCell(
                createNoBorderCell("联系方式" + ":"
                        + header.element("联系方式").text))
        table.addCell(
                createNoBorderCell("订购日期" + ":"
                        + header.element("订购日期").text))

        table.addCell(
                createNoBorderCell("收货地址" + ":"
                        + header.element("收货地址").text, 4))
        pdfDoc.add(table)

        pdfDoc.add(PdfUtils.getBParagraph(STITLE_SIZE, "\n"))


        //获取中间表格的数据，即产品列表
        val centerElements = header.element("bbb").elements()


        val table1: PdfPTable

        if (supplier == "北京大学化学院器材办") {
            table1 = PdfPTable(11)
        } else {
            table1 = PdfPTable(12)
        }

        table1.addCell(createCell("序号"))

        if (supplier != "北京大学化学院器材办") {
            table1.addCell(createCell("供货商"))
        }

        table1.addCell(createCell("品牌"))

        table1.addCell(createCell("货号"))

        table1.addCell(createCell("品名", 2))
        table1.addCell(createCell("规格"))

        table1.addCell(createCell("质量等级"))
        table1.addCell(createCell("单价(元)"))
        table1.addCell(createCell("数量"))

        table1.addCell(createCell("总价(元)"))
        table1.addCell(createCell("项目号"))

        var counter = 1
        for (e in centerElements) {
            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, counter.toString()))
            if (supplier != "北京大学化学院器材办") {
                table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("供货商").text))
            }
            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("品牌").text))

            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("货号").text))
            table1.addCell(createCell(e.element("品名").text, 2))

            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("规格").text))

            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("质量等级").text))
            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("单价").text))

            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("数量").text))
            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("总价").text))
            table1.addCell(PdfUtils.getBParagraph(STITLE_SIZE, e.element("项目号").text))
            counter++
        }
        pdfDoc.add(table1)

        //合计
        val sum = PdfUtils.getBParagraph(STITLE_SIZE, "合计" + ":"
                + header.element("合计").text + "                  ")
        sum.alignment = Element.ALIGN_RIGHT
        pdfDoc.add(sum)

        pdfDoc.add(createBelowTable("操作员"
                + ":" + header.element("操作员").text + "\n\n"))

        pdfDoc.add(createBelowTable("出库员"
                + ":" + header.element("出库员").text + "\n\n"))

        pdfDoc.add(createBelowTable("送货人"
                + ":" + header.element("送货人").text + "\n\n"))

        if (insertSign) {
            val table2 = PdfPTable(11)
            table2.addCell(createNoBorderCell("领用人：" + "\n\n"))
            table2.addCell(createImageCell())
            pdfDoc.add(table2)

        } else {
            val table2 = PdfPTable(1)
            table2.addCell(createNoBorderCell("领用人：" + "\n\n"))
            pdfDoc.add(table2)
        }

        pdfDoc.add(createBelowTable("日期"
                + ":" + "\n\n"))
        pdfDoc.close()
        return path
    }

    fun createBelowTable(str: String): PdfPTable {
        val table2 = PdfPTable(1)
        table2.addCell(createNoBorderCell(str))
        return table2
    }

    fun createImageCell(): PdfPCell {
        val image = Image.getInstance(Environment
                .getExternalStorageDirectory().path + "/SignApp2/" + "a.png")
        image.setAbsolutePosition(100f, 480f)
        image.scaleAbsolute(54f, 28f)
        val cell0 = PdfPCell(image, false)

        cell0.borderColor = BaseColor.WHITE
        cell0.colspan = 10
        return cell0
    }

    fun createNoBorderCell(str: String, colspan: Int = 1): PdfPCell {
        val cell0 = PdfPCell(PdfUtils.getBParagraph(STITLE_SIZE, str))
        cell0.colspan = colspan
        cell0.borderColor = BaseColor.WHITE
        return cell0
    }

    fun createCell(str: String, colspan: Int = 1): PdfPCell {

        //要求是表头无边框
        val cel1 = PdfPCell(PdfUtils.getBParagraph(STITLE_SIZE, str))
        cel1.colspan = colspan

        return cel1
    }
}

