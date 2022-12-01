package cryptography

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color
import java.util.*

fun getHideBit(value: Int, position: Int, orVal: Int): Int {
    return ((orVal shr 1) shl 1) + ((value shr position) and 1);
}

fun hideMsgInImage() {
    println("Input image file:")
    val inImg = readln()
    println("Output image file:")
    val outImg = readln()
    println("Message to hide:")
    var hideMsg = readln().encodeToByteArray()
    println("Password:")
    val pass = readln().encodeToByteArray()
    for (i in 0..hideMsg.lastIndex) {
        hideMsg[i] = (hideMsg[i].toInt() xor pass[i%pass.size].toInt()).toByte()
    }
    hideMsg += byteArrayOf(0,0,3)
    var i = 0
    try {
        val myImage: BufferedImage = ImageIO.read(File(inImg))
        if (hideMsg.size*8 > myImage.width*myImage.height) throw Exception("The input image is not large enough to hold this message.")
        main@ for (y in 0 until myImage.height) {
            for (x in 0 until myImage.width) {
                val color = Color(myImage.getRGB(x, y))
                myImage.setRGB(x, y, Color(color.red,color.green,getHideBit(hideMsg[i / 8].toInt(),7-i % 8,color.blue)).rgb)
                i += 1
                if (i == hideMsg.size*8) break@main
            }
        }
        ImageIO.write(myImage, "png", File(outImg))
        println("Message saved in $outImg image.")
    } catch (e: Exception) {
        println(e.message+"\n${hideMsg.lastIndex}\n"+e.stackTrace.joinToString("\n"))
    }
}

fun getUnHideBit(value: Int, position: Int, orVal: Int): Int {
    return value + ((orVal and 1) shl position);
}

fun showMsgInImage() {
    val endMsg = byteArrayOf(0,0,3).joinToString()
    println("Input image file:")
    val inImg = readln()
    println("Password:")
    val pass = readln().encodeToByteArray()
    try {
        val myImage: BufferedImage = ImageIO.read(File(inImg))
        var hideMsg = ByteArray(myImage.width*myImage.height)
        var i = 0
        main@ for (y in 0 until myImage.height) {
            for (x in 0 until myImage.width) {
                val color = Color(myImage.getRGB(x, y))
                hideMsg[i / 8] = getUnHideBit(hideMsg[i / 8].toInt(),7-i % 8,color.blue).toByte()
                i += 1
                if (i >= 24 && endMsg == hideMsg.copyOfRange(i / 8 - 3, i / 8).joinToString()) break@main
            }
        }
        hideMsg = hideMsg.copyOfRange(0, i / 8 - 3)
        for (i in 0..hideMsg.lastIndex) {
            hideMsg[i] = (hideMsg[i].toInt() xor pass[i%pass.size].toInt()).toByte()
        }
        println("Message:\n ${hideMsg.toString(Charsets.UTF_8)}")
    } catch (e: Exception) {
        println(e.message)
    }
}

fun main() {
    do {
        println("Task (hide, show, exit):")
        when (val rds = readln()) {
            "hide" -> hideMsgInImage()
            "show" -> showMsgInImage()
            "exit" -> break
            else -> println("Wrong task: $rds")
        }

    }  while (true)
    println("Bye!")
}

