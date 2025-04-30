package com.example.yi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.yi.ui.theme.YiTheme
import com.nlf.calendar.Lunar;
import java.util.Date
import java.time.LocalDateTime

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    private var currentScreen = mutableStateOf(0)
    
    companion object {
        private const val PREFS_NAME = "device_prefs"
        private const val DEVICE_UUID_KEY = "device_uuid"
        
        val IMMUTABLE_MAP = mapOf(
            "子" to 1,
            "丑" to 2,
            "寅" to 3,
            "卯" to 4,
            "辰" to 5,
            "巳" to 6,
            "午" to 7,
            "未" to 8,
            "申" to 9,
            "酉" to 10,
            "戌" to 11,
            "亥" to 12
        )

        val BG_MAP = listOf(
            "000",
            "111",
            "011",
            "101",
            "001",
            "110",
            "010",
            "100"
        )

        val HEXAGRAM_MAP = mapOf(
            // 二进制键规则：前3位=上卦(六五三爻)，后3位=下卦(四二一爻)
            // 爻序方向：字符串从左到右对应 上爻(六) → 五爻 → 四爻 → 三爻 → 二爻 → 初爻(一)

            /* 乾宫八卦（上卦为乾111） */
            "111111" to "乾",   // 上乾下乾，乾为天
            "111110" to "姤",   // 上乾下巽，天风姤（初爻变阴 111|110）
            "111100" to "遁",   // 上乾下艮，天山遁（二爻变阴 111|100）
            "111000" to "否",   // 上乾下坤，天地否（三爻变阴 111|000）
            "110000" to "观",   // 上巽下坤，风地观（四爻变阴 110|000）
            "100000" to "剥",   // 上艮下坤，山地剥（五爻变阴 100|000）
            "101000" to "晋",   // 上离下坤，火地晋（四爻返阳 101|000）
            "101111" to "大有", // 上离下乾，火天大有（归魂卦 101|111）

            /* 坤宫八卦（上卦为坤000） */
            "000000" to "坤",   // 上坤下坤，坤为地
            "000001" to "复",   // 上坤下震，地雷复（初爻变阳 000|001）
            "000011" to "临",   // 上坤下兑，地泽临（二爻变阳 000|011）
            "000111" to "泰",   // 上坤下乾，地乾泰（三爻变阳 000|111）
            "001111" to "大壮", // 上震下乾，雷天大壮（四爻变阳 001|111）
            "011111" to "夬",   // 上兑下乾，泽天夬（五爻变阳 011|111）
            "010111" to "需",   // 上坎下乾，水天需（四爻变阴 010|111）
            "010000" to "比",   // 上坎下坤，水地比（归魂卦 010|000）

            /* 震宫八卦（上卦为震001） */
            "001001" to "震",   // 上震下震，震为雷
            "001000" to "豫",   // 上震下坤，雷地豫（初爻变阴 001|000）
            "001010" to "解",   // 上震下坎，雷水解（二爻变阳 001|010）
            "001110" to "恒",   // 上震下巽，雷风恒（三爻变阳 001|110）
            "000110" to "升",   // 上地下巽，地风升（四爻变阴 000|110）
            "010110" to "井",   // 上坎下巽，水风井（五爻变阳 010|110）
            "011110" to "大过", // 上兑下巽，泽风大过（四爻返阳 011|110）
            "011001" to "随",   // 上兑下震，泽雷随（归魂卦 011|001）

            /* 巽宫八卦（上卦为巽110） */
            "110110" to "巽",   // 上巽下巽，巽为风
            "110111" to "小畜", // 上巽下乾，风天小畜（初爻变阳 110|111）
            "110101" to "家人", // 上巽下离，风火家人（二爻变阳 110|101）
            "110001" to "益",   // 上巽下震，风雷益（三爻变阳 110|001）
            "111001" to "无妄", // 上乾下震，天雷无妄（四爻变阳 111|001）
            "101001" to "噬嗑", // 上离下震，火雷噬嗑（五爻变阴 101|001）
            "100001" to "颐",   // 上艮下震，山雷颐（四爻变阴 100|001）
            "100110" to "蛊",   // 上艮下巽，山风蛊（归魂卦 100|110）

            /* 坎宫八卦（上卦为坎010） */
            "010010" to "坎",   // 上坎下坎，坎为水
            "010011" to "节",   // 上坎下兑，水泽节（初爻变阳 010|011）
            "010001" to "屯",   // 上坎下震，水雷屯（二爻变阳 010|001）
            "010101" to "既济", // 上坎下离，水火既济（三爻变阳 010|101）
            "011101" to "革",   // 上兑下离，泽火革（四爻变阳 011|101）
            "001101" to "丰",   // 上震下离，雷火丰（五爻变阴 001|101）
            "000101" to "明夷", // 上坤下离，地火明夷（四爻变阴 000|101）
            "000010" to "师",   // 上坤下坎，地水师（归魂卦 000|010）

            /* 离宫八卦（上卦为离101） */
            "101101" to "离",   // 上离下离，离为火
            "101100" to "旅",   // 上离下艮，火山旅（初爻变阴 101|100）
            "101110" to "鼎",   // 上离下巽，火风鼎（二爻变阳 101|110）
            "101010" to "未济", // 上离下坎，火水未济（三爻变阴 101|010）
            "100010" to "蒙",   // 上艮下坎，山水蒙（四爻变阴 100|010）
            "110010" to "涣",   // 上巽下坎，风水涣（五爻变阳 110|010）
            "111010" to "讼",   // 上乾下坎，天水讼（四爻返阳 111|010）
            "111101" to "同人", // 上乾下离，天火同人（归魂卦 111|101）

            /* 艮宫八卦（上卦为艮100） */
            "100100" to "艮",   // 上艮下艮，艮为山
            "100101" to "贲",   // 上艮下离，山火贲（初爻变阳 100|101）
            "100111" to "大畜", // 上艮下乾，山天大畜（二爻变阳 100|111）
            "100011" to "损",   // 上艮下兑，山泽损（三爻变阳 100|011）
            "101011" to "睽",   // 上离下兑，火泽睽（四爻变阳 101|011）
            "111011" to "履",   // 上乾下兑，天泽履（五爻变阴 111|011）
            "110011" to "中孚", // 上巽下兑，风泽中孚（四爻变阴 110|011）
            "110100" to "渐",   // 上巽下艮，风山渐（归魂卦 110|100）

            /* 兑宫八卦（上卦为兑011） */
            "011011" to "兑",   // 上兑下兑，兑为泽
            "011010" to "困",   // 上兑下坎，泽水困（初爻变阴 011|010）
            "011000" to "萃",   // 上兑下坤，泽地萃（二爻变阴 011|000）
            "011100" to "咸",   // 上兑下艮，泽山咸（三爻变阴 011|100）
            "010100" to "蹇",   // 上坎下艮，水山蹇（四爻变阳 010|100）
            "000100" to "谦",   // 上坤下艮，地山谦（五爻变阳 000|100）
            "001100" to "小过", // 上震下艮，雷山小过（四爻变阳 001|100）
            "001011" to "归妹"  // 上震下兑，雷泽归妹（归魂卦 001|011）
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 获取或生成设备UUID
        val uuid = getOrCreateDeviceUuid()
        
        enableEdgeToEdge()
        setContent {
            YiTheme {
                currentScreen
                val scrollState = rememberScrollState()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen.value) {
                        0 -> MainScreen { screen -> currentScreen.value = screen }
                        1 -> Greeting(qiu = "姻缘", uuid = uuid, tian = 21, modifier = Modifier.padding(innerPadding).fillMaxWidth().verticalScroll(scrollState))
                        2 -> Greeting(qiu = "事业", uuid = uuid, tian = 13, modifier = Modifier.padding(innerPadding).fillMaxWidth().verticalScroll(scrollState))
                        3 -> Greeting(qiu = "健康", uuid = uuid, tian = 20, modifier = Modifier.padding(innerPadding).fillMaxWidth().verticalScroll(scrollState))
                        4 -> Greeting(qiu = "（改成想问的）", uuid = uuid, tian = 0, modifier = Modifier.padding(innerPadding).fillMaxWidth().verticalScroll(scrollState))
                        // 添加其他功能页面的case
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (currentScreen.value != 0) {
            currentScreen.value = 0
        } else {
            super.onBackPressed()
        }
    }
    
    private fun getOrCreateDeviceUuid(): String {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        var uuid = prefs.getString(DEVICE_UUID_KEY, null)
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString()
            prefs.edit().putString(DEVICE_UUID_KEY, uuid).apply()
        }
        return uuid
    }
}

@Composable
fun MainScreen(onButtonClick: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onButtonClick(1) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "姻缘")
        }
        Button(
            onClick = { onButtonClick(2) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "事业")
        }
        Button(
            onClick = { onButtonClick(3) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "健康")
        }
        Button(
            onClick = { onButtonClick(4) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "随心问")
        }
    }
}

@Composable
fun YaoxianPainter(
    binaryStr: String,
    baseColor: Color,
    highlightIndices: Set<Int> = emptySet(),
    highlightColor: Color = Color.Red
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(0.6f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        binaryStr.forEachIndexed { index, c ->
            // 判断当前行用什么颜色
            val lineColor = if (index in highlightIndices) highlightColor else baseColor

            if (c == '1') {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    thickness = 8.dp,
                    color = lineColor
                )
            } else {
                BrokenLine(
                    modifier = Modifier.height(8.dp),
                    color = lineColor,
                    thickness = 8.dp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BrokenLine(
    modifier: Modifier = Modifier,
    color: Color,
    thickness: Dp,
    gapFraction: Float = 0.16f // 原来是0.2f，现在中间更小一点点
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        val totalWidth = size.width
        val segmentWidth = (totalWidth * (1f - gapFraction)) / 2f
        val gapWidth = totalWidth * gapFraction

        // 左边段
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(segmentWidth, size.height / 2),
            strokeWidth = thickness.toPx()
        )

        // 右边段
        drawLine(
            color = color,
            start = Offset(segmentWidth + gapWidth, size.height / 2),
            end = Offset(totalWidth, size.height / 2),
            strokeWidth = thickness.toPx()
        )
    }
}

@Composable
fun Greeting(qiu: String, uuid: String, tian: Int, modifier: Modifier = Modifier) {
    SelectionContainer {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally) {
            val guaList = Bu(uuid, tian)

            Text(
                text = "主卦：${MainActivity.HEXAGRAM_MAP[guaList[0]]}"
            )
            YaoxianPainter(binaryStr = guaList[0], baseColor = MaterialTheme.colorScheme.onBackground)

            Text(
                text = "互卦：${MainActivity.HEXAGRAM_MAP[guaList[1]]}"
            )
            YaoxianPainter(binaryStr = guaList[1], baseColor = MaterialTheme.colorScheme.onBackground)

            Text(
                text = "变卦：${MainActivity.HEXAGRAM_MAP[guaList[2]]}"
            )
            YaoxianPainter(binaryStr = guaList[2], baseColor = MaterialTheme.colorScheme.onBackground,
                highlightIndices = setOf(guaList[3].toInt()), // 高亮
                highlightColor = Color.Red)

            Text(
                text = """AI解卦文案：
            你是一位精通《周易》的国学专家，擅长结合卦象结构、爻辞原文和现实情境进行综合分析。你的解卦风格需具备：  
            - **专业性**：准确引用卦爻辞原文（如《象传》《彖传》关键句）  
            - **实用性**：将卦理映射到用户具体问题（如事业、感情等）  
            - **辩证性**：指出卦象中的矛盾与转化条件
            你需要做的是：
            1. **卦象结构分析**  
            - 主卦、互卦、变卦的**五行生克关系**（如乾金克巽木）  
            - 关键爻位（如动爻、世应爻）的阴阳属性与象征意义  
            2. **卦爻辞映射**  
            - 结合用户问题，选取**不超过3条核心爻辞**逐句解读（例：乾卦九三“君子终日乾乾”对应职场压力）  
            - 说明爻位关系（如中正、乘承、比应）对结果的影响  
            3. **现实推演建议**  
            - 用“机会-风险”模型提炼决策方向（如：“主动沟通（乾卦）可破局，但需避免冒进（大过卦‘栋桡’警告）”）  
            - 给出1个短期行动建议和1个长期策略
            需要你将结果用分栏对比形式输出，包含以下模块：
            1.卦象格局
            2.核心爻辞
            3.行动指南	
            4.趋势走向
            5.完整总结

            想在我的问题是：我起得主卦${MainActivity.HEXAGRAM_MAP[guaList[0]]}，互卦${MainActivity.HEXAGRAM_MAP[guaList[1]]}，变卦${MainActivity.HEXAGRAM_MAP[guaList[2]]}，想问我未来的${qiu}情况
            """
            )
        }
    }
}

fun Bu(uuid: String, tian: Int) : List<String>{

    // 获取当前公历日期
    val solarDate = Date() // 或自定义日期：Date(2024-1900, 5-1, 15)（注意 Date 的年份从 1900 开始计数）
    
    // 转换为农历对象
    val lunarDate = Lunar.fromDate(solarDate)
         
    // 提取农历信息
    val lunarYearZhi = lunarDate.getYearZhi()       // 农历年份

    val lunarMonth = lunarDate.month      // 农历月份
    val lunarDay = lunarDate.day          // 农历日
    val year = MainActivity.IMMUTABLE_MAP[lunarYearZhi]

    // 使用UUID的hashCode作为唯一数值标识
    val decimalValue = uuid.hashCode().toLong()
    println("decimalValue: $decimalValue")

    val hour: Int = LocalDateTime.now().hour
    var zhi = (hour / 2) + (hour % 2)
    zhi = if (zhi < 12) zhi + 1 else 0 + 1

    val sG = ((decimalValue + tian.toLong() + year!!.toLong() + lunarMonth.toLong() + lunarDay.toLong()) % 8).toInt().absoluteValue % 8
    val xG = ((decimalValue + tian.toLong() + year!!.toLong() + lunarMonth.toLong() + lunarDay.toLong() + zhi.toLong()) % 8).toInt().absoluteValue % 8
    val bY = ((decimalValue + tian.toLong() + year!!.toLong() + lunarMonth.toLong() + lunarDay.toLong() + zhi.toLong()) % 6).toInt().absoluteValue % 6
    val size = if (bY == 0) 0 else (6 - bY)

    println("sG: $sG, xG: $xG, bY: $bY")

    val zhuG = MainActivity.BG_MAP[sG.toInt()] + MainActivity.BG_MAP[xG.toInt()]
    val bianY = if ((MainActivity.BG_MAP[sG.toInt()] + MainActivity.BG_MAP[xG.toInt()])[size] == '1') '0' else '1'
    var bianGChars = (MainActivity.BG_MAP[sG.toInt()] + MainActivity.BG_MAP[xG.toInt()]).toCharArray()
    bianGChars[size] = bianY
    val bianG = String(bianGChars)
    val huG = MainActivity.BG_MAP[sG.toInt()].substring(1) + MainActivity.BG_MAP[xG.toInt()].substring(0,1) + MainActivity.BG_MAP[sG.toInt()].substring(2) + MainActivity.BG_MAP[xG.toInt()].substring(0,2)
    return listOf(zhuG,huG,bianG,size.toString())
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YiTheme {
        //Greeting("Android")
    }
}