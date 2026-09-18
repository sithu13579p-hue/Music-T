package com.example.data

import com.example.model.SongInfo
import com.example.model.SongLyricLine
import com.example.model.TranslationResult
import com.example.model.TranslationStyle

object SampleSongData {

    val sampleSong1 = SongInfo(
        title = "Perfect (Ballad)",
        fileName = "ed_sheeran_perfect_sample.mp3",
        durationMs = 45000,
        format = "MP3",
        sizeFormatted = "3.2 MB",
        detectedLanguage = "English (en-US)",
        isSample = true
    )

    val sampleSong2 = SongInfo(
        title = "Spring Day (봄날)",
        fileName = "bts_spring_day_sample.m4a",
        durationMs = 42000,
        format = "M4A",
        sizeFormatted = "2.8 MB",
        detectedLanguage = "Korean (ko-KR)",
        isSample = true
    )

    val sampleSong3 = SongInfo(
        title = "Yoru ni Kakeru (夜に駆ける)",
        fileName = "yoasobi_racing_sample.wav",
        durationMs = 40000,
        format = "WAV",
        sizeFormatted = "5.1 MB",
        detectedLanguage = "Japanese (ja-JP)",
        isSample = true
    )

    val sampleSong4 = SongInfo(
        title = "Tong Hua / Fairytale (童话)",
        fileName = "michael_wong_tonghua.mp3",
        durationMs = 44000,
        format = "MP3",
        sizeFormatted = "3.6 MB",
        detectedLanguage = "Mandarin (zh-CN)",
        isSample = true
    )

    val allSamples = listOf(sampleSong1, sampleSong2, sampleSong3, sampleSong4)

    fun getSampleResult(song: SongInfo, style: TranslationStyle, keepEmotion: Boolean): TranslationResult {
        return when (song.title) {
            sampleSong2.title -> getKoreanSample(style, keepEmotion)
            sampleSong3.title -> getJapaneseSample(style, keepEmotion)
            sampleSong4.title -> getChineseSample(style, keepEmotion)
            else -> getEnglishSample(style, keepEmotion)
        }
    }

    private fun getEnglishSample(style: TranslationStyle, keepEmotion: Boolean): TranslationResult {
        val lines = when (style) {
            TranslationStyle.POETIC -> listOf(
                SongLyricLine(1, 0, 4500, "I found a love, for me", "ငါ့နှလုံးသားအတွက် ချစ်ခြင်းမေတ္တာ စစ်စစ်တစ်ခုကို ရှာဖွေတွေ့ရှိခဲ့ပြီ"),
                SongLyricLine(2, 4500, 9200, "Darling, just dive right in and follow my lead", "အချစ်ရယ်... သံသယမရှိ ငါ့လက်ကိုဆွဲပြီး လိုက်ခဲ့ပါ"),
                SongLyricLine(3, 9200, 14000, "Well, I found a girl, beautiful and sweet", "သိပ်လှပပြီး ချိုမြိန်ကြည်နူးဖွယ် ကောင်မလေးတစ်ယောက်ကို ငါတွေ့ဆုံခဲ့တယ်"),
                SongLyricLine(4, 14000, 19500, "Oh, I never knew you were the someone waiting for me", "ငါ့ဘဝအတွက် စောင့်ကြိုနေမယ့်သူဟာ မင်းလေးမှန်း မသိခဲ့မိဘူး"),
                SongLyricLine(5, 19500, 24500, "'Cause we were just kids when we fell in love", "တို့နှစ်ယောက် ချစ်စခင်စတုန်းက ကလေးဘဝ အပြစ်ကင်းစင်နေတုန်းမို့"),
                SongLyricLine(6, 24500, 29000, "Not knowing what it was", "ချစ်ခြင်းရဲ့ အတိမ်အနက်ကို မခန့်မှန်းတတ်ခဲ့ကြဘူး"),
                SongLyricLine(7, 29000, 35000, "I will not give you up this time", "ဒီတစ်ကြိမ်မှာတော့ မင်းလက်ကို ဘယ်တော့မှ လက်လွှတ်မခံတော့ပါဘူး"),
                SongLyricLine(8, 35000, 42000, "Darling, just hold my hand, be my girl, I'll be your man", "အချစ်ရေ ငါ့လက်ကိုဆုပ်ကိုင်ထားပါ၊ မင်းရဲ့အနားမှာ ငါအမြဲရှိနေပါ့မယ်"),
                SongLyricLine(9, 42000, 45000, "I see my future in your eyes", "မင်းရဲ့ မျက်ဝန်းတွေထဲမှာ ငါ့ရဲ့အနာဂတ်ကို မြင်တွေ့နေရပြီ")
            )
            TranslationStyle.LITERAL -> listOf(
                SongLyricLine(1, 0, 4500, "I found a love, for me", "ငါသည် ငါ့အတွက် အချစ်တစ်ခုကို ရှာတွေ့ခဲ့သည်"),
                SongLyricLine(2, 4500, 9200, "Darling, just dive right in and follow my lead", "ချစ်သူ၊ အတွင်းသို့ ခုန်ဆင်းပြီး ငါ့နောက်သို့ လိုက်ခဲ့ပါ"),
                SongLyricLine(3, 9200, 14000, "Well, I found a girl, beautiful and sweet", "ငါသည် လှပပြီး ချိုသာသော မိန်းကလေးတစ်ယောက်ကို ရှာတွေ့ခဲ့သည်"),
                SongLyricLine(4, 14000, 19500, "Oh, I never knew you were the someone waiting for me", "အို၊ သင်သည် ငါ့ကို စောင့်ဆိုင်းနေသောသူဖြစ်သည်ကို ငါမသိခဲ့ပါ"),
                SongLyricLine(5, 19500, 24500, "'Cause we were just kids when we fell in love", "အဘယ်ကြောင့်ဆိုသော် တို့ချစ်ကြစဉ်က ကလေးများသာ ဖြစ်ကြသောကြောင့်"),
                SongLyricLine(6, 24500, 29000, "Not knowing what it was", "၎င်းသည် အဘယ်အရာဖြစ်သည်ကို မသိရှိခဲ့ပါ"),
                SongLyricLine(7, 29000, 35000, "I will not give you up this time", "ဒီတစ်ကြိမ်တွင် ငါသည် သင့်ကို လက်လျှော့မည်မဟုတ်ပါ"),
                SongLyricLine(8, 35000, 42000, "Darling, just hold my hand, be my girl, I'll be your man", "ချစ်သူ၊ ငါ့လက်ကို ကိုင်ပါ၊ ငါ့မိန်းကလေးဖြစ်ပါ၊ ငါသည် သင်၏လူ ဖြစ်မည်"),
                SongLyricLine(9, 42000, 45000, "I see my future in your eyes", "ငါသည် သင်၏ မျက်လုံးများထဲတွင် ငါ့အနာဂတ်ကို မြင်သည်")
            )
            TranslationStyle.EASY -> listOf(
                SongLyricLine(1, 0, 4500, "I found a love, for me", "ငါ့အတွက် အချစ်စစ်ကို ရှာတွေ့ခဲ့ပြီ"),
                SongLyricLine(2, 4500, 9200, "Darling, just dive right in and follow my lead", "ချစ်ရသူရေ... ငါ့နောက်ကိုပဲ လိုက်ခဲ့ပါ"),
                SongLyricLine(3, 9200, 14000, "Well, I found a girl, beautiful and sweet", "ချစ်စရာကောင်းပြီး သိပ်လှတဲ့ ကောင်မလေးကို ငါတွေ့ခဲ့တယ်"),
                SongLyricLine(4, 14000, 19500, "Oh, I never knew you were the someone waiting for me", "မင်းက ငါ့ကိုစောင့်နေတဲ့သူမှန်း မသိခဲ့ရိုးအမှန်ပါ"),
                SongLyricLine(5, 19500, 24500, "'Cause we were just kids when we fell in love", "ငယ်ငယ်တုန်းက ချစ်မိတော့ ဘာမှမသိနားမလည်ခဲ့ဘူး"),
                SongLyricLine(6, 24500, 29000, "Not knowing what it was", "အချစ်ဆိုတာ ဘာလဲမသိခဲ့ဘူး"),
                SongLyricLine(7, 29000, 35000, "I will not give you up this time", "ဒီတစ်ခါတော့ မင်းကို အဆုံးရှုံးမခံတော့ဘူး"),
                SongLyricLine(8, 35000, 42000, "Darling, just hold my hand, be my girl, I'll be your man", "ငါ့လက်ကို ကိုင်ထားပါ၊ တို့နှစ်ယောက် အမြဲအတူတူရှိမယ်"),
                SongLyricLine(9, 42000, 45000, "I see my future in your eyes", "မင်းမျက်လုံးလေးတွေထဲမှာ ငါ့အနာဂတ်ကို မြင်နေရတယ်")
            )
            else -> listOf( // NATURAL
                SongLyricLine(1, 0, 4500, "I found a love, for me", "ငါ့ဘဝအတွက် တကယ်ချစ်ရမယ့်သူကို ရှာတွေ့ခဲ့ပြီ"),
                SongLyricLine(2, 4500, 9200, "Darling, just dive right in and follow my lead", "အချစ်ရေ စိတ်ချလက်ချနဲ့ ငါ့လက်ကိုတွဲပြီး လိုက်ခဲ့ပါ"),
                SongLyricLine(3, 9200, 14000, "Well, I found a girl, beautiful and sweet", "သိပ်ကို လှပကြည်နူးစရာကောင်းတဲ့ ကောင်မလေးတစ်ယောက်ကို တွေ့ဆုံခဲ့တယ်"),
                SongLyricLine(4, 14000, 19500, "Oh, I never knew you were the someone waiting for me", "ငါ့အတွက် ဖူးစာရှင်ဟာ မင်းလေးမှန်း အရင်က မသိခဲ့ဘူး"),
                SongLyricLine(5, 19500, 24500, "'Cause we were just kids when we fell in love", "တို့နှစ်ယောက် စချစ်ကြတုန်းက အရွယ်မရောက်သေးတဲ့ ကလေးတွေပဲမို့"),
                SongLyricLine(6, 24500, 29000, "Not knowing what it was", "အချစ်ရဲ့ အဓိပ္ပာယ်ကို သေသေချာချာ မသိနားမလည်ခဲ့ဘူး"),
                SongLyricLine(7, 29000, 35000, "I will not give you up this time", "ဒီတစ်ခေါက်မှာတော့ မင်းကို ဘယ်တော့မှ လက်လွှတ်မဆုံးရှုံးစေရဘူး"),
                SongLyricLine(8, 35000, 42000, "Darling, just hold my hand, be my girl, I'll be your man", "ငါ့လက်ကို ခိုင်ခိုင်ဆုပ်ကိုင်ထားပါ၊ မင်းကို ငါ အမြဲစောင့်ရှောက်သွားမယ်"),
                SongLyricLine(9, 42000, 45000, "I see my future in your eyes", "မင်းရဲ့ မျက်ဝန်းတွေထဲမှာ ငါတို့နှစ်ယောက်ရဲ့ အနာဂတ်ကို မြင်နေရပြီ")
            )
        }

        return TranslationResult(
            songTitle = "Perfect",
            detectedLanguage = "English (en-US)",
            confidence = 0.98f,
            styleUsed = style,
            keepEmotion = keepEmotion,
            lines = lines
        )
    }

    private fun getKoreanSample(style: TranslationStyle, keepEmotion: Boolean): TranslationResult {
        val lines = listOf(
            SongLyricLine(1, 0, 4200, "보고 싶다 (bogo sipda)", "မင်းကို သိပ်လွမ်းတယ်"),
            SongLyricLine(2, 4200, 8500, "이렇게 말하니까 더 보고 싶다", "ဒီလိုထုတ်ပြောလိုက်တော့ ပိုပြီးတောင် လွမ်းလာရတယ်"),
            SongLyricLine(3, 8500, 14200, "너희 사진을 보고 있어도 보고 싶다", "မင်းဓာတ်ပုံလေးတွေကို ကြည့်နေရတာတောင် လွမ်းစိတ်က မပြေဘူး"),
            SongLyricLine(4, 14200, 19000, "야속한 시간, 나는 우리가 밉다", "ရက်စက်တဲ့ အချိန်ကာလတွေရယ်... တို့နှစ်ယောက်ကို မုန်းမိတယ်"),
            SongLyricLine(5, 19000, 24000, "이젠 얼굴 한 번 보는 것도", "အခုတော့ မျက်နှာချင်းဆိုင် တစ်ခါတွေ့ဖို့တောင်"),
            SongLyricLine(6, 24000, 29500, "힘들어진 우리가", "တို့နှစ်ယောက်အတွက် သိပ်ခက်ခဲသွားခဲ့ပြီ"),
            SongLyricLine(7, 29500, 35500, "추운 겨울 끝을 지나", "အေးစက်တဲ့ ဆောင်းရာသီကုန်ဆုံးသွားပြီးနောက်"),
            SongLyricLine(8, 35500, 42000, "다시 봄날이 올 때까지 [unclear]", "နွေဦးနေ့ရက်တွေ ပြန်ရောက်လာတဲ့အထိ စောင့်နေမယ်")
        )
        return TranslationResult(
            songTitle = "Spring Day (봄날)",
            detectedLanguage = "Korean (ko-KR)",
            confidence = 0.96f,
            styleUsed = style,
            keepEmotion = keepEmotion,
            lines = lines
        )
    }

    private fun getJapaneseSample(style: TranslationStyle, keepEmotion: Boolean): TranslationResult {
        val lines = listOf(
            SongLyricLine(1, 0, 4500, "沈むように溶けてゆくように", "မှောင်မိုက်ခြင်းထဲ နစ်မြုပ်သွားသလို ပျော်ဝင်သွားသလို"),
            SongLyricLine(2, 4500, 9500, "二人だけの空が広がる夜に", "တို့နှစ်ယောက်တည်းရဲ့ ကောင်းကင် ကျယ်ပြန့်နေတဲ့ ညမှာ"),
            SongLyricLine(3, 9500, 14800, "「さよなら」だけだった", "\"နှုတ်ဆက်ပါတယ်\" ဆိုတဲ့ စကားတစ်ခွန်းတည်းနဲ့ပဲ"),
            SongLyricLine(4, 14800, 20000, "その一言で全てが伝わった", "အရာအားလုံးကို သဘောပေါက် နားလည်သွားခဲ့တယ်"),
            SongLyricLine(5, 20000, 25200, "日の沈んだ空と夜の狭間に", "နေဝင်ရီတရော ကောင်းကင်နဲ့ ညယံကြားမှာ"),
            SongLyricLine(6, 25200, 31000, "フェンス越しに重なる二人の影", "ခြံစည်းရိုးကိုကျော်ပြီး ထပ်တူကျနေတဲ့ တို့နှစ်ယောက်ရဲ့ အရိပ်လေးတွေ"),
            SongLyricLine(7, 31000, 36500, "初めて会った日から", "မင်းနဲ့ စတင်တွေ့ဆုံခဲ့တဲ့ နေ့ကတည်းက"),
            SongLyricLine(8, 36500, 40000, "僕の心の全てを奪った", "ငါ့နှလုံးသားတစ်ခုလုံးကို မင်းသိမ်းပိုက်သွားခဲ့တာပါ")
        )
        return TranslationResult(
            songTitle = "Yoru ni Kakeru (夜に駆ける)",
            detectedLanguage = "Japanese (ja-JP)",
            confidence = 0.97f,
            styleUsed = style,
            keepEmotion = keepEmotion,
            lines = lines
        )
    }

    private fun getChineseSample(style: TranslationStyle, keepEmotion: Boolean): TranslationResult {
        val lines = listOf(
            SongLyricLine(1, 0, 5000, "忘了有多久 再没听到你", "မင်းရဲ့ အသံလေးကို မကြားရတာ ဘယ်လောက်ကြာသွားပြီလဲ မေ့နေခဲ့တယ်"),
            SongLyricLine(2, 5000, 10500, "对我说你 最爱的故事", "မင်းအကြိုက်ဆုံး ဒဏ္ဍာရီပုံပြင်လေးကို ငါ့ကိုပြောပြဖူးခဲ့တာ"),
            SongLyricLine(3, 10500, 16000, "我想了很久 我开始慌了", "ငါ အချိန်အကြာကြီး စဉ်းစားရင်း ရင်ထဲ ကြောက်ရွံ့စိုးရိမ်လာမိတယ်"),
            SongLyricLine(4, 16000, 22000, "是不是我又 做错了什么", "ငါတစ်ခုခုများ ထပ်မှားမိသွားလို့လားလို့"),
            SongLyricLine(5, 22000, 28000, "你哭着对我说 童话里都是骗人的", "မင်းက မျက်ရည်တွေနဲ့ ပြောခဲ့တယ်... ဒဏ္ဍာရီပုံပြင်တွေဆိုတာ အားလုံး အလိမ်အညာတွေချည်းပါပဲတဲ့"),
            SongLyricLine(6, 28000, 34000, "我不可能是你的王子", "ငါဟာ မင်းရဲ့ မင်းသားလေး မဖြစ်နိုင်ဘူးတဲ့"),
            SongLyricLine(7, 34000, 40000, "也许你不会懂 从你说爱我以后", "မင်းငါ့ကို ချစ်တယ်လို့ ပြောခဲ့တဲ့အချိန်ကစပြီး မင်းနားမလည်နိုင်ခဲ့တာတွေရှိတယ်"),
            SongLyricLine(8, 40000, 44000, "我的天空 星星都亮了", "ငါ့ရဲ့ ကောင်းကင်ယံမှာ ကြယ်တွေအားလုံး လင်းလက်တောက်ပလာခဲ့တာပါ")
        )
        return TranslationResult(
            songTitle = "Tong Hua / Fairytale (童话)",
            detectedLanguage = "Mandarin (zh-CN)",
            confidence = 0.99f,
            styleUsed = style,
            keepEmotion = keepEmotion,
            lines = lines
        )
    }
}
