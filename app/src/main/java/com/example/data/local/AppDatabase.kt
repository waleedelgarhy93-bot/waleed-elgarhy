package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DailyLogEntry
import com.example.data.model.IncidentStatus
import com.example.data.model.IncidentType
import com.example.data.model.School
import com.example.data.model.SchoolStage
import com.example.data.model.SecurityIncident
import com.example.data.model.SeverityLevel
import com.example.data.model.Teacher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [School::class, Teacher::class, SecurityIncident::class, DailyLogEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
    abstract fun teacherDao(): TeacherDao
    abstract fun securityIncidentDao(): SecurityIncidentDao
    abstract fun dailyLogDao(): DailyLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "samalut_security_db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }
    }
}

suspend fun populateInitialData(database: AppDatabase) {
    val schoolDao = database.schoolDao()
    val teacherDao = database.teacherDao()
    val incidentDao = database.securityIncidentDao()
    val logDao = database.dailyLogDao()

    if (schoolDao.getCount() == 0) {
        val sampleSchools = listOf(
            School(
                name = "مدرسة سمالوط الثانوية بنين",
                stage = SchoolStage.SECONDARY,
                classroomsCount = 28,
                staffCount = 92,
                principalName = "أ/ عادل عبد المجيد",
                vicePrincipalName = "أ/ طارق عبد الله",
                address = "شارع الجمهورية - بندر سمالوط",
                phone = "0863821040",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "مدرسة كبرى بها 1400 طالب، تحتاج متابعة أمنية عند الانصراف"
            ),
            School(
                name = "مدرسة سمالوط الثانوية بنات",
                stage = SchoolStage.SECONDARY,
                classroomsCount = 26,
                staffCount = 85,
                principalName = "أ/ نادية صادق إبراهيم",
                vicePrincipalName = "أ/ منال رفعت",
                address = "حي غرب - بجوار الإدارة التعليمية، سمالوط",
                phone = "0863823412",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "انضباط عالي، كاميرات مراقبة على البوابات الرئيسية"
            ),
            School(
                name = "مدرسة بني غني الثانوية المشتركة",
                stage = SchoolStage.SECONDARY,
                classroomsCount = 18,
                staffCount = 56,
                principalName = "أ/ كمال سيد عبد الوهاب",
                vicePrincipalName = "أ/ سامي محروس",
                address = "قرية بني غني - مركز سمالوط",
                phone = "0863845011",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "موقع ريفي، تأمين محيط المدرسة الخارجي"
            ),
            School(
                name = "مدرسة سمالوط الإعدادية بنين",
                stage = SchoolStage.PREPARATORY,
                classroomsCount = 24,
                staffCount = 74,
                principalName = "أ/ ممدوح فوزي مرقص",
                vicePrincipalName = "أ/ جمال حميدة",
                address = "شارع المدارس - سمالوط",
                phone = "0863822189",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "كثافة طلابية عالية في الفصول"
            ),
            School(
                name = "مدرسة سمالوط الإعدادية بنات",
                stage = SchoolStage.PREPARATORY,
                classroomsCount = 22,
                staffCount = 68,
                principalName = "أ/ هدى عبد الرازق",
                vicePrincipalName = "أ/ إيمان حسني",
                address = "ميدان المحطة - سمالوط",
                phone = "0863821900",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "قريبة من خط السكة الحديد، متابعة حركة الطالبات"
            ),
            School(
                name = "مدرسة قلوصنا الإعدادية المشتركة",
                stage = SchoolStage.PREPARATORY,
                classroomsCount = 16,
                staffCount = 48,
                principalName = "أ/ عبد الرحمن متولي",
                vicePrincipalName = "أ/ حسن خليفة",
                address = "قرية قلوصنا - سمالوط",
                phone = "0863870025",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "تنسيق مستمر مع نقطة شرطة قلوصنا"
            ),
            School(
                name = "مدرسة الشهيد أحمد كمال الابتدائية",
                stage = SchoolStage.PRIMARY,
                classroomsCount = 20,
                staffCount = 62,
                principalName = "أ/ حسام الدين لطفي",
                vicePrincipalName = "أ/ سميحة نجيب",
                address = "شارع المحطة - سمالوط",
                phone = "0863820455",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "تأمين خروج التلاميذ لمنع التدافع أمام الطريق السريع"
            ),
            School(
                name = "مدرسة العهد الجديد الابتدائية",
                stage = SchoolStage.PRIMARY,
                classroomsCount = 18,
                staffCount = 54,
                principalName = "أ/ رمسيس حليم عطالله",
                vicePrincipalName = "أ/ ماريان وجيه",
                address = "شارع الجيش - سمالوط",
                phone = "0863821123",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "حالة أمنية مستقرة وبيئة مدرسية متميزة"
            ),
            School(
                name = "مدرسة شوشة الابتدائية المشتركة",
                stage = SchoolStage.PRIMARY,
                classroomsCount = 14,
                staffCount = 42,
                principalName = "أ/ أحمد عبد العليم شحاتة",
                vicePrincipalName = "أ/ محمود غانم",
                address = "قرية شوشة - سمالوط",
                phone = "0863866100",
                securityOfficerName = "مستر مصطفى فرغلي",
                notes = "تمت معاينة سور المدرسة وأمان النوافذ"
            )
        )
        schoolDao.insertSchools(sampleSchools)
    }

    if (teacherDao.getCount() == 0) {
        val sampleTeachers = listOf(
            Teacher(
                name = "أ/ أحمد محمود الشريف",
                schoolName = "مدرسة سمالوط الثانوية بنين",
                stage = SchoolStage.SECONDARY,
                jobTitle = "معلم خبير رياضيات",
                phone = "01004567891",
                nationalId = "28504121700112",
                subject = "رياضيات",
                hireDate = "2008/10/01",
                address = "سمالوط - حي الزهور"
            ),
            Teacher(
                name = "أ/ محمد كمال الدين عبد الباقي",
                schoolName = "مدرسة سمالوط الثانوية بنين",
                stage = SchoolStage.SECONDARY,
                jobTitle = "أخصائي اجتماعي أول",
                phone = "01123456780",
                nationalId = "28902041700445",
                subject = "تربية اجتماعية",
                hireDate = "2012/03/15",
                address = "سمالوط - شارع الثورة"
            ),
            Teacher(
                name = "أ/ فاطمة حسن الزهراء",
                schoolName = "مدرسة سمالوط الثانوية بنات",
                stage = SchoolStage.SECONDARY,
                jobTitle = "معلم أول أ فيزياء",
                phone = "01289012345",
                nationalId = "28706181700321",
                subject = "فيزياء",
                hireDate = "2010/09/01",
                address = "سمالوط - شارع المستشفى"
            ),
            Teacher(
                name = "أ/ هشام صبحي عبد الله",
                schoolName = "مدرسة سمالوط الإعدادية بنين",
                stage = SchoolStage.PREPARATORY,
                jobTitle = "معلم لغة عربية",
                phone = "01099887766",
                nationalId = "29201011700876",
                subject = "لغة عربية",
                hireDate = "2016/11/01",
                address = "قرية التوفيقية - سمالوط"
            ),
            Teacher(
                name = "أ/ رانيا نبيل جرجس",
                schoolName = "مدرسة سمالوط الإعدادية بنات",
                stage = SchoolStage.PREPARATORY,
                jobTitle = "معلم أول لغة إنجليزية",
                phone = "01201234567",
                nationalId = "28812251700234",
                subject = "لغة إنجليزية",
                hireDate = "2011/10/01",
                address = "سمالوط - شارع النيل"
            ),
            Teacher(
                name = "أ/ عبد الله مسعود علي",
                schoolName = "مدرسة الشهيد أحمد كمال الابتدائية",
                stage = SchoolStage.PRIMARY,
                jobTitle = "معلم دراسات اجتماعية",
                phone = "01155443322",
                nationalId = "29405061700654",
                subject = "دراسات اجتماعية",
                hireDate = "2018/02/01",
                address = "سمالوط - شارع السلام"
            ),
            Teacher(
                name = "أ/ سارة مجدي يوسف",
                schoolName = "مدرسة العهد الجديد الابتدائية",
                stage = SchoolStage.PRIMARY,
                jobTitle = "معلمة فصل",
                phone = "01066778899",
                nationalId = "29609151700789",
                subject = "معلم فصل",
                hireDate = "2020/01/15",
                address = "سمالوط - شارع المحطة"
            )
        )
        teacherDao.insertTeachers(sampleTeachers)
    }

    if (incidentDao.getCount() == 0) {
        val sampleIncidents = listOf(
            SecurityIncident(
                title = "مشادة وتدافع بين طلاب بالفسحة",
                incidentType = IncidentType.STUDENT_STUDENT,
                description = "حدوث مشادة كلامية تطورت إلى تدافع بين طلاب الصف الأول الثانوي أثناء فترة الفسحة بفناء المدرسة، وتدخل الأخصائي الاجتماعي وإدارة الأمن فوراً.",
                schoolName = "مدرسة سمالوط الثانوية بنين",
                partiesInvolved = "الطالب يوسف محمود x الطالب عمر مصطفى",
                severity = SeverityLevel.MEDIUM,
                actionTaken = "تم اصطحاب الطلاب لمكتب الأمن والملاحظة، واستدعاء وليي الأمر وأخذ إقرار كتابي بالانضباط والتصالح بالتراضي.",
                status = IncidentStatus.RESOLVED,
                dateString = "2026/10/14",
                timeString = "11:20",
                supervisor = "مستر مصطفى فرغلي"
            ),
            SecurityIncident(
                title = "شكوى ولي أمر بشأن معاملة صفية",
                incidentType = IncidentType.PARENT,
                description = "حضور ولي أمر طالب بالصف الثاني الإعدادي معترضاً على درجة النشاط ومطالباً بمقابلة مدير المدرسة.",
                schoolName = "مدرسة سمالوط الإعدادية بنين",
                partiesInvolved = "ولي الأمر أحمد عادل x معلم المادة أ/ هشام صبحي",
                severity = SeverityLevel.LOW,
                actionTaken = "استقبال ولي الأمر بمعرفة مسؤول الأمن وإدارة المدرسة وشرح آلية التقييم، وتفهم ولي الأمر وحل الموضوع ودياً.",
                status = IncidentStatus.CLOSED,
                dateString = "2026/10/13",
                timeString = "09:45",
                supervisor = "مستر مصطفى فرغلي"
            ),
            SecurityIncident(
                title = "متابعة أمان البوابة الخارجية وقت الانصراف",
                incidentType = IncidentType.VIOLATION,
                description = "تجمع لبعض الشباب غير المنتسبين للمدرسة أمام البوابة الخارجية وقت خروج الطالبات بمدرسة الإعدادية بنات.",
                schoolName = "مدرسة سمالوط الإعدادية بنات",
                partiesInvolved = "تجمعات شبابية أمام السور الخارجي",
                severity = SeverityLevel.HIGH,
                actionTaken = "التواصل الفوري مع نقطة الشرطة والدورية الميدانية لتأمين المحيط وفض التجمعات حتى انصراف كافة الطالبات بسلام.",
                status = IncidentStatus.RESOLVED,
                dateString = "2026/10/12",
                timeString = "14:10",
                supervisor = "مستر مصطفى فرغلي"
            )
        )
        for (incident in sampleIncidents) {
            incidentDao.insertIncident(incident)
        }
    }

    val sampleLogs = listOf(
        DailyLogEntry(
            dateString = "2026/10/15",
            schoolName = "مدرسة سمالوط الثانوية بنين",
            teacherName = "أ/ أحمد محمود الشريف",
            note = "مرور ميداني صباحي للتأكد من انتظام طابور الصباح وخلو الفناء من أي معوقات أمنية.",
            supervisor = "مستر مصطفى فرغلي"
        ),
        DailyLogEntry(
            dateString = "2026/10/15",
            schoolName = "مدرسة الشهيد أحمد كمال الابتدائية",
            teacherName = "أ/ عبد الله مسعود علي",
            note = "متابعة تنظيم دخول التلاميذ صباحاً مع مشرفي الأدوار وسلامة مخارج الطوارئ.",
            supervisor = "مستر مصطفى فرغلي"
        )
    )
    for (log in sampleLogs) {
        logDao.insertLog(log)
    }
}
