package org.iii.library.Definition;


public class Final {

    //身份
    public final static String fa = "a";//admin
    public final static String fm = "m";//mem
    public final static String fu = "u";//user

    //主層
    public final static String M = "member";//會員資料
    public final static String N = "news";//消息資料
    public final static String B = "book";//所有書籍資料(單筆資料：編號)
    public final static String C = "comment";//所有評論資料
    public final static String R = "record";//所有借閱資料

    //次層
    //public final static String CU = "count";//書籍數量
    //public final static String BN = "name";//查詢條件：書名
    //public final static String CLS = "class";//查詢條件：分類
    public final static String I = "ISBN";//查詢條件：ISBN
    public final static String F = "forget";//忘記密碼
    //public final static String GA = "getadmin";//確認管理員
    //public final static String GU = "getuser";//確認會員
    //public final static String S = "search";//查詢
    public final static String rm = "rolemember";//管理員身份→會員身份
    public final static String ra = "roleadmin";//會員身份→管理員身份
    //public final static String SF = "self";//會員評論
    public final static String RD = "returndate";//還書功能
    public final static String SH = "selfhouse";//歷史記錄
    public final static String IBA = "inborrowall";//借閱中書籍

    //三層
    //public final static String CA = "count/all";//所有書籍總數
    public final static String CI = "count/inborrow";//借閱中總數


    //按鈕名稱
    private final static String news = "最新\n消息";
    private final static String search = "查詢\n館藏";
    private final static String login = "登入";
    private final static String logout = "登出";
    private final static String system = "系統\n設定";
    private final static String edit = "修改\n密碼";
    private final static String personal = "個人\n書房";
    private final static String defend = "系統\n維護";

    //使用者
    public final static String[] guest = {news, search, login, system};
    //會員
    public final static String[] mem = {news, search, logout, system, personal, edit};
    //管理員
    public final static String[] ad = {news, search, logout, system, personal, edit, defend};

    //連結A↔B
    public final static int fromNews = 111;
    public final static int fromSearch = 222;
    public final static int fromRecord = 333;
    public final static int fromHistory = 444;
    public final static int fromBookManage = 555;

    /**錯誤訊息*/
    public final static String EMPTY = "空值";
    public final static String URL_ERROR = "尚未連接";
    public final static String NOT_FOUND = "資料不存在";
    public final static String INTERNAL_ERROR = "內部錯誤";
    public final static String NEW_SCS = "新增成功";
    public final static String NEW_FAL = "新增失敗";
    public final static String UPDATE_SCS = "修改成功";
    public final static String UPDATE_FAL = "變更失敗";
    public final static String DEL_SCS = "刪除成功";
    public final static String DEL_FAL = "刪除失敗";
    public final static String IO = "I/O ERROR";
    public final static String JSON_ERROR = "資料有誤";
    public final static String NOT_RUN = "未執行";

    /*search*/
    public final static String KEYWORD_EMPTY = "請輸入關鍵字";

    /*login*/
    public final static String MEMBER_ERROR = "帳號或密碼有誤";

    /*forget*/
    public final static String ACC_EMPTY = "請輸入帳號";
    public final static String EMAIL_EMPTY = "請輸入email\n(ex：xxx@gamil.com)";
    public final static String EMAIL_FAL = "請輸入正確的email格式\n(ex：xxx@gamil.com)";
    public final static String DATA_INPUT_ERROR = "資料有誤，\n請檢查輸入的資料為正確";
    public final static String DATA_ERROR = "資料有誤";
    public final static String MAIL_ERROR = "寄信有誤";

    /*edit*/
    public final static String SECRET_INCONSISTENT = "與舊密碼不相符";

    /*news manage*/
    public final static String NEWS_LENGTH_OVER = "最大長度限制：100個字";

    /*權限*/
    private int decide;

    //建構子
    public Final(int decide){
        this.decide = decide;
    }

    //取得權限
    public String getLimit(){
        return setLimit(decide);
    }
    //設置權限
    private String setLimit(int decide){
        switch(decide){
            case 1:
                return fa;
            case 2:
                return fm;
            default:
                return fu;
        }
    }




}
