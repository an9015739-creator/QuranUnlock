package com.quranunlock.app.data

object QuranRepository {

    const val TOTAL_QURAN_VERSES = 6236

    val SURAHS: List<Surah> = listOf(
        Surah(1, "Al-Fatihah", "الفاتحة", "الفاتحہ (آغاز)", 7, 1),
        Surah(2, "Al-Baqarah", "البقرة", "البقرہ (گائے)", 286, 8),
        Surah(3, "Ali 'Imran", "آل عمران", "آل عمران", 200, 294),
        Surah(4, "An-Nisa", "النساء", "النساء (عورتیں)", 176, 494),
        Surah(5, "Al-Ma'idah", "المائدة", "المائدہ (دسترخوان)", 120, 670),
        Surah(6, "Al-An'am", "الأنعام", "الانعام (مویشی)", 165, 790),
        Surah(7, "Al-A'raf", "الأعراف", "الاعراف (بلندی والی جگہ)", 206, 955),
        Surah(8, "Al-Anfal", "الأنفال", "الانفال (غنیمت)", 75, 1161),
        Surah(9, "At-Tawbah", "التوبة", "التوبہ (توبہ)", 129, 1236),
        Surah(10, "Yunus", "يونس", "یونس علیہ السلام", 109, 1365),
        Surah(11, "Hud", "هود", "ہود علیہ السلام", 123, 1474),
        Surah(12, "Yusuf", "يوسف", "یوسف علیہ السلام", 111, 1597),
        Surah(13, "Ar-Ra'd", "الرعد", "الرعد (بادل کی گرج)", 43, 1708),
        Surah(14, "Ibrahim", "إبراهيم", "ابراہیم علیہ السلام", 52, 1751),
        Surah(15, "Al-Hijr", "الحجر", "الحجر (پتھریلی چٹان)", 99, 1803),
        Surah(16, "An-Nahl", "النحل", "النحل (شہد کی مکھی)", 128, 1902),
        Surah(17, "Al-Isra", "الإسراء", "بنی اسرائیل / الاسراء", 111, 2030),
        Surah(18, "Al-Kahf", "الكهف", "الکہف (غار)", 110, 2141),
        Surah(19, "Maryam", "مريم", "مریم علیھا السلام", 98, 2251),
        Surah(20, "Taha", "طه", "طٰہٰ", 135, 2349),
        Surah(21, "Al-Anbiya", "الأنبياء", "الانبیاء (انبیاء کرام)", 112, 2484),
        Surah(22, "Al-Hajj", "الحج", "الحج (حج کی عبادت)", 78, 2596),
        Surah(23, "Al-Mu'minun", "المؤمنون", "المؤمنون (اہل ایمان)", 118, 2674),
        Surah(24, "An-Nur", "النور", "النور (روشنی)", 64, 2792),
        Surah(25, "Al-Furqan", "الفرقان", "الفرقان (حق و باطل میں فرق)", 77, 2856),
        Surah(26, "Ash-Shu'ara", "الشعراء", "الشعراء (شاعر)", 227, 2933),
        Surah(27, "An-Naml", "النمل", "النمل (چیونٹی)", 93, 3160),
        Surah(28, "Al-Qasas", "القصص", "القصص (واقعات)", 88, 3253),
        Surah(29, "Al-'Ankabut", "العنكبوت", "العنکبوت (مکڑی)", 69, 3341),
        Surah(30, "Ar-Rum", "الروم", "الروم (اہل روم)", 60, 3410),
        Surah(31, "Luqman", "لقمان", "لقمان حکیم", 34, 3470),
        Surah(32, "As-Sajdah", "السجدة", "السجدہ (سجدہ)", 30, 3504),
        Surah(33, "Al-Ahzab", "الأحزاب", "الاحزاب (لشکر)", 73, 3534),
        Surah(34, "Saba", "سبإ", "سبا", 54, 3607),
        Surah(35, "Fatir", "فاطر", "فاطر (پیدا کرنے والا)", 45, 3661),
        Surah(36, "Ya-Sin", "يس", "یٰسٓ", 83, 3706),
        Surah(37, "As-Saffat", "الصافات", "الصافات (صف باندھنے والے)", 182, 3789),
        Surah(38, "Sad", "ص", "صٓ", 88, 3971),
        Surah(39, "Az-Zumar", "الزمر", "الزمر (جتھے / گروہ)", 75, 4059),
        Surah(40, "Ghafir", "غافر", "المؤمن / غافر", 85, 4134),
        Surah(41, "Fussilat", "فصلت", "حم السجدہ / فصلت", 54, 4219),
        Surah(42, "Ash-Shura", "الشورى", "الشورٰی (مشورہ)", 53, 4273),
        Surah(43, "Az-Zukhruf", "الزخرف", "الزخرف (سونے کی سجاوٹ)", 89, 4326),
        Surah(44, "Ad-Dukhan", "الدخان", "الدخان (دھواں)", 59, 4415),
        Surah(45, "Al-Jathiyah", "الجاثية", "الجاثیہ (گھٹنوں کے بل گرے ہوئے)", 37, 4474),
        Surah(46, "Al-Ahqaf", "الأحقاف", "الاحقاف (ریت کے ٹیلے)", 35, 4511),
        Surah(47, "Muhammad", "محمد", "محمد ﷺ", 38, 4546),
        Surah(48, "Al-Fath", "الفتح", "الفتح (کھلی فتح)", 29, 4584),
        Surah(49, "Al-Hujurat", "الحجرات", "الحجرات (حجرے / کمرے)", 18, 4613),
        Surah(50, "Qaf", "ق", "قٓ", 45, 4631),
        Surah(51, "Adh-Dhariyat", "الذاريات", "الذاریات (اڑانے والی ہوائیں)", 60, 4676),
        Surah(52, "At-Tur", "الطور", "الطور (طور پہاڑ)", 49, 4736),
        Surah(53, "An-Najm", "النجم", "النجم (ستارہ)", 62, 4785),
        Surah(54, "Al-Qamar", "القمر", "القمر (چاند)", 55, 4847),
        Surah(55, "Ar-Rahman", "الرحمن", "الرحمن (نہایت رحم والا)", 78, 4902),
        Surah(56, "Al-Waqi'ah", "الواقعة", "الواقعہ (قیامت)", 96, 4980),
        Surah(57, "Al-Hadid", "الحديد", "الحدید (لوہا)", 29, 5076),
        Surah(58, "Al-Mujadila", "المجادلة", "المجادلہ (بحث کرنے والی)", 22, 5105),
        Surah(59, "Al-Hashr", "الحشر", "الحشر (اکٹھا کرنا)", 24, 5127),
        Surah(60, "Al-Mumtahanah", "الممتحنة", "الممتحنہ (جانچی جانے والی)", 13, 5151),
        Surah(61, "As-Saf", "الصف", "الصف (سیدھی قطار)", 14, 5164),
        Surah(62, "Al-Jumu'ah", "الجمعة", "الجمعہ (نماز جمعہ)", 11, 5178),
        Surah(63, "Al-Munafiqun", "المنافقون", "المنافقون (منافق لوگ)", 11, 5189),
        Surah(64, "At-Taghabun", "التغابن", "التغابن (ہار جیت)", 18, 5200),
        Surah(65, "At-Talaq", "الطلاق", "الطلاق (طلاق)", 12, 5218),
        Surah(66, "At-Tahrim", "التحريم", "التحریم (حرام ٹھہرانا)", 12, 5230),
        Surah(67, "Al-Mulk", "الملك", "الملک (بادشاہی)", 30, 5242),
        Surah(68, "Al-Qalam", "القلم", "القلم (قلم)", 52, 5272),
        Surah(69, "Al-Haqqah", "الحاقة", "الحاقہ (سچی قیامت)", 52, 5324),
        Surah(70, "Al-Ma'arij", "المعارج", "المعارج (عروج کے راستے)", 44, 5376),
        Surah(71, "Nuh", "نوح", "نوح علیہ السلام", 28, 5420),
        Surah(72, "Al-Jinn", "الجن", "الجن (پوشیدہ مخلوق)", 28, 5448),
        Surah(73, "Al-Muzzammil", "المزمل", "المزمل (کمبل اوڑھنے والے)", 20, 5476),
        Surah(74, "Al-Muddaththir", "المدثر", "المدثر (چادر اوڑھنے والے)", 56, 5496),
        Surah(75, "Al-Qiyamah", "القيامة", "القیامہ (قیامت)", 40, 5552),
        Surah(76, "Al-Insan", "الإنسان", "الانسان / الدھر", 31, 5592),
        Surah(77, "Al-Mursalat", "المرسلات", "المرسلات (بھیجی جانے والی ہوائیں)", 50, 5623),
        Surah(78, "An-Naba", "النبإ", "النباء (بڑی خبر)", 40, 5673),
        Surah(79, "An-Nazi'at", "النازعات", "النازعات (کھینچنے والے فرشتے)", 46, 5713),
        Surah(80, "'Abasa", "عبس", "عبس (تیوری چڑھائی)", 42, 5759),
        Surah(81, "At-Takwir", "التكوير", "التکویر (لپیٹا جانا)", 29, 5801),
        Surah(82, "Al-Infitar", "الانفطار", "الانفطار (پھٹ جانا)", 19, 5830),
        Surah(83, "Al-Mutaffifin", "المطففين", "المطففین (ناپ تول میں کمی)", 36, 5849),
        Surah(84, "Al-Inshiqaq", "الانشقاق", "الانشقاق (ٹکڑے ٹکڑے ہونا)", 25, 5885),
        Surah(85, "Al-Buruj", "البروج", "البروج (آسمانی قلعے)", 22, 5910),
        Surah(86, "At-Tariq", "الطارق", "الطارق (رات کو آنے والا تارا)", 17, 5932),
        Surah(87, "Al-A'la", "الأعلى", "الاعلیٰ (سب سے برتر)", 19, 5949),
        Surah(88, "Al-Ghashiyah", "الغاشية", "الغاشیہ (چھانے والی مصیبت)", 26, 5968),
        Surah(89, "Al-Fajr", "الفجر", "الفجر (صبح کی پو پھوٹنا)", 30, 5994),
        Surah(90, "Al-Balad", "البلد", "البلد (مقدس شہر مکہ)", 20, 6024),
        Surah(91, "Ash-Shams", "الشمس", "الشمس (سورج)", 15, 6044),
        Surah(92, "Al-Layl", "الليل", "اللیل (رات)", 21, 6059),
        Surah(93, "Ad-Duha", "الضحى", "الضحیٰ (چڑھتا سورج)", 11, 6080),
        Surah(94, "Ash-Sharh", "الشرح", "الشرح (سینے کا کھلنا)", 8, 6091),
        Surah(95, "At-Tin", "التين", "التین (انجیر)", 8, 6099),
        Surah(96, "Al-'Alaq", "العلق", "العلق (جما ہوا خون)", 19, 6107),
        Surah(97, "Al-Qadr", "القدر", "القدر (قدر والی رات)", 5, 6126),
        Surah(98, "Al-Bayyinah", "البينة", "البینہ (واضح دلیل)", 8, 6131),
        Surah(99, "Az-Zalzalah", "الزلزلة", "الزلزلہ (زمین کا زلزلہ)", 8, 6139),
        Surah(100, "Al-'Adiyat", "العاديات", "العادیات (دوڑنے والے گھوڑے)", 11, 6147),
        Surah(101, "Al-Qari'ah", "القارعة", "القارcount", 11, 6158),
        Surah(102, "At-Takathur", "التكاثر", "التکاثر (زیادتی کی ہوس)", 8, 6169),
        Surah(103, "Al-'Asr", "العصر", "العصر (زمانہ)", 3, 6177),
        Surah(104, "Al-Humazah", "الهمزة", "الہمزہ (عیب جوئی کرنے والا)", 9, 6180),
        Surah(105, "Al-Fil", "الفيل", "الفیل (ہاتھی والے)", 5, 6189),
        Surah(106, "Quraysh", "قريش", "قریش", 4, 6194),
        Surah(107, "Al-Ma'un", "الماعون", "الماعون (عام برتنے کی چیزیں)", 7, 6198),
        Surah(108, "Al-Kawthar", "الكوثر", "الکوثر (حوض کوثر)", 3, 6205),
        Surah(109, "Al-Kafirun", "الكافرون", "الکافرون (کافر لوگ)", 6, 6208),
        Surah(110, "An-Nasr", "النصر", "النصر (اللہ کی مدد)", 3, 6214),
        Surah(111, "Al-Masad", "المسد", "لہب / المسد (کھجور کی چھال)", 5, 6217),
        Surah(112, "Al-Ikhlas", "الإخلاص", "الاخلاص (خالص توحید)", 4, 6222),
        Surah(113, "Al-Falaq", "الفلق", "الفلق (صبح کا اجالا)", 5, 6226),
        Surah(114, "An-Nas", "الناس", "الناس (انسان)", 6, 6231)
    )

    fun getSurah(surahNumber: Int): Surah {
        return SURAHS.find { it.number == surahNumber } ?: SURAHS[0]
    }

    /**
     * Calculates the subsequent verse in rigorous chronological Quran order.
     * When Surah ends, automatically progresses to Surah + 1, Ayah 1.
     * After Surah 114, Ayah 6, cycles smoothly back to Surah 1, Ayah 1 (Khatm e Quran).
     */
    fun getNextVerseCoordinates(currentSurah: Int, currentAyah: Int): Pair<Int, Int> {
        val surah = getSurah(currentSurah)
        return if (currentAyah < surah.ayahCount) {
            Pair(currentSurah, currentAyah + 1)
        } else {
            if (currentSurah < 114) {
                Pair(currentSurah + 1, 1)
            } else {
                Pair(1, 1) // Completed Quran! Cycle to start
            }
        }
    }

    fun getVerse(surahNumber: Int, ayahNumber: Int, reciterFolder: String, urduFolder: String): QuranVerse {
        val surah = getSurah(surahNumber)
        val globalIndex = surah.startAyahGlobal + ayahNumber - 1

        val formattedSurah = String.format("%03d", surahNumber)
        val formattedAyah = String.format("%03d", ayahNumber)
        val filename = "$formattedSurah$formattedAyah.mp3"

        val arabicUrl = "https://everyayah.com/data/$reciterFolder/$filename"
        val urduUrl = "https://everyayah.com/data/$urduFolder/$filename"

        return QuranVerse(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            surahName = surah.name,
            arabicName = surah.arabicName,
            urduName = surah.urduName,
            globalAyahIndex = globalIndex,
            arabicAudioUrl = arabicUrl,
            urduAudioUrl = urduUrl
        )
    }
}
