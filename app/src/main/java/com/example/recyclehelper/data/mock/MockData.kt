package com.example.recyclehelper.data.mock

import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.DaySchedule
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.data.model.RegionSchedule
import java.time.DayOfWeek

/**
 * API 연동 전 사용하는 임시(mock) 데이터.
 * 실제 환경에서는 기후에너지환경부 / 행정안전부 API 로 대체된다.
 */
object MockData {

    val items: List<RecycleItem> = listOf(

        // ───────── 재활용품 ─────────
        RecycleItem(
            id = "pet",
            name = "페트병",
            category = Category.RECYCLABLE,
            disposalMethod = "내용물을 비우고 물로 헹군 후, 라벨을 제거하고 압축하여 배출",
            disposalLocation = "재활용품 수거함",
            cautions = listOf(
                "뚜껑과 라벨은 일반쓰레기로 분리",
                "이물질이 묻은 경우 물로 헹궈야 함",
                "찌그러뜨려서 부피를 줄이면 좋음"
            )
        ),
        RecycleItem(
            id = "styrofoam",
            name = "스티로폼",
            category = Category.RECYCLABLE,
            disposalMethod = "이물질·테이프·운송장을 제거하고 깨끗한 상태로 배출",
            disposalLocation = "재활용품 수거함 (스티로폼 분리 배출)",
            cautions = listOf(
                "색깔 있는 스티로폼·오염된 것은 일반쓰레기",
                "컵라면 용기 등 음식물 묻은 것은 헹군 후 배출",
                "테이프·스티커는 반드시 제거"
            )
        ),
        RecycleItem(
            id = "glass_bottle",
            name = "유리병",
            category = Category.RECYCLABLE,
            disposalMethod = "내용물을 비우고 헹군 후 뚜껑을 분리하여 배출",
            disposalLocation = "유리류 전용 수거함",
            cautions = listOf(
                "깨진 유리는 신문지에 싸서 일반쓰레기로",
                "거울·도자기·내열유리는 재활용 불가",
                "보증금 환불 대상 병은 매장 반납 가능"
            )
        ),
        RecycleItem(
            id = "vinyl",
            name = "비닐",
            category = Category.RECYCLABLE,
            disposalMethod = "이물질을 제거하고 투명·반투명 비닐을 모아서 배출",
            disposalLocation = "비닐류 전용 수거함",
            cautions = listOf(
                "음식물·이물질이 묻은 비닐은 일반쓰레기",
                "작은 비닐은 한데 모아 묶어서 배출",
                "은박지·랩은 재활용 불가"
            )
        ),
        RecycleItem(
            id = "can",
            name = "캔",
            category = Category.RECYCLABLE,
            disposalMethod = "내용물을 비우고 헹군 후 압축하여 배출",
            disposalLocation = "캔류 전용 수거함",
            cautions = listOf(
                "부탄가스·살충제 캔은 구멍을 뚫어 가스 제거 후 배출",
                "담배꽁초 등 이물질 넣지 않기",
                "철캔·알루미늄캔 함께 배출 가능"
            )
        ),
        RecycleItem(
            id = "newspaper",
            name = "신문지",
            category = Category.RECYCLABLE,
            disposalMethod = "물에 젖지 않게 반듯하게 펴서 묶은 후 배출",
            disposalLocation = "종이류 전용 수거함",
            cautions = listOf(
                "비닐 코팅된 전단지는 일반쓰레기",
                "물에 젖은 신문지는 재활용 불가",
                "끈으로 십자 묶음 하면 수거가 편리"
            )
        ),
        RecycleItem(
            id = "cardboard",
            name = "택배 박스",
            category = Category.RECYCLABLE,
            disposalMethod = "테이프·운송장·완충재를 모두 제거한 후 접어서 배출",
            disposalLocation = "종이류 전용 수거함",
            cautions = listOf(
                "테이프는 반드시 떼어내기",
                "비닐 완충재(뽁뽁이)는 비닐류로 분리",
                "젖은 박스는 말려서 배출하거나 일반쓰레기"
            )
        ),
        RecycleItem(
            id = "milk_carton",
            name = "우유팩",
            category = Category.RECYCLABLE,
            disposalMethod = "내용물을 비우고 물로 헹군 후 펼쳐서 말린 뒤 배출",
            disposalLocation = "종이팩 전용 수거함 (일반 종이류와 분리)",
            cautions = listOf(
                "일반 종이와 섞지 않기 (코팅 재질이 달라 별도 재활용)",
                "주스팩·두유팩도 동일하게 배출",
                "충분히 헹궈야 냄새와 오염 방지"
            )
        ),
        RecycleItem(
            id = "plastic_container",
            name = "플라스틱 용기",
            category = Category.RECYCLABLE,
            disposalMethod = "내용물을 비우고 헹군 후 라벨을 제거하여 배출",
            disposalLocation = "플라스틱류 전용 수거함",
            cautions = listOf(
                "용기 바닥 재질 표시(PP, PE, PS 등) 확인",
                "펌프·뚜껑은 분리하여 배출",
                "이물질이 제거 안 되면 일반쓰레기"
            )
        ),
        RecycleItem(
            id = "clothing",
            name = "헌 옷",
            category = Category.RECYCLABLE,
            disposalMethod = "깨끗하게 세탁한 후 의류수거함에 배출",
            disposalLocation = "의류수거함 (아파트 단지·주민센터 근처)",
            cautions = listOf(
                "이불·베개·카펫은 대형폐기물로 배출",
                "속옷·양말은 일반쓰레기",
                "젖은 옷은 건조 후 배출"
            )
        ),
        RecycleItem(
            id = "paper_cup",
            name = "종이컵",
            category = Category.RECYCLABLE,
            disposalMethod = "내용물을 비우고 헹군 후 종이컵끼리 모아 배출",
            disposalLocation = "종이팩 전용 수거함 또는 종이컵 수거함",
            cautions = listOf(
                "안쪽 코팅 때문에 일반 종이류와 분리",
                "빨대·리드(뚜껑)는 일반쓰레기",
                "커피 찌꺼기는 반드시 비우기"
            )
        ),
        RecycleItem(
            id = "metal",
            name = "고철",
            category = Category.RECYCLABLE,
            disposalMethod = "이물질을 제거하고 캔류·고철류 수거함에 배출",
            disposalLocation = "캔·고철류 수거함",
            cautions = listOf(
                "옷걸이·냄비·프라이팬 등 금속 제품 해당",
                "플라스틱 손잡이 등 이질 재질은 분리",
                "녹슨 고철도 재활용 가능"
            )
        ),
        RecycleItem(
            id = "pet_transparent",
            name = "투명 페트병",
            category = Category.RECYCLABLE,
            disposalMethod = "라벨 제거, 내용물 비우기, 압축 후 뚜껑 닫아 별도 배출",
            disposalLocation = "투명 페트병 별도 수거함",
            cautions = listOf(
                "유색 페트병과 반드시 분리",
                "2021년부터 별도 분리배출 의무화",
                "뚜껑은 닫아서 배출 (압축 상태 유지)"
            )
        ),
        RecycleItem(
            id = "aluminum_foil",
            name = "알루미늄 호일",
            category = Category.RECYCLABLE,
            disposalMethod = "음식물을 제거한 뒤 구겨서 캔류 수거함에 배출",
            disposalLocation = "캔류 전용 수거함",
            cautions = listOf(
                "음식물이 심하게 눌어붙은 것은 일반쓰레기",
                "은박지 코팅 종이(과자 봉지 등)는 일반쓰레기",
                "깨끗한 호일만 재활용 가능"
            )
        ),

        // ───────── 일반쓰레기 ─────────
        RecycleItem(
            id = "chicken_box",
            name = "치킨박스",
            category = Category.GENERAL,
            disposalMethod = "기름이 묻은 부분은 일반쓰레기로, 깨끗한 부분만 종이류로 배출",
            disposalLocation = "일반쓰레기 종량제 봉투 / 종이류 수거함",
            cautions = listOf(
                "기름·양념이 밴 종이는 재활용 불가",
                "내부 받침대(코팅 종이)는 일반쓰레기",
                "비닐·뼈는 반드시 분리"
            )
        ),
        RecycleItem(
            id = "tissue",
            name = "휴지",
            category = Category.GENERAL,
            disposalMethod = "사용한 휴지는 종량제 봉투에 넣어 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "사용하지 않은 깨끗한 휴지도 재활용 불가 (물에 녹는 재질)",
                "물티슈는 별도 — 일반쓰레기로 배출",
                "화장실 휴지는 변기에 버려도 됨 (수용성)"
            )
        ),
        RecycleItem(
            id = "receipt",
            name = "영수증",
            category = Category.GENERAL,
            disposalMethod = "종량제 봉투에 넣어 일반쓰레기로 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "감열지(열에 반응하는 종이)라 재활용 불가",
                "일반 종이류에 절대 섞지 않기",
                "ATM 명세표·복권도 동일"
            )
        ),
        RecycleItem(
            id = "diaper",
            name = "기저귀",
            category = Category.GENERAL,
            disposalMethod = "오물을 변기에 버린 후 종량제 봉투에 넣어 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "내용물(대변)은 변기에 처리 후 배출",
                "성인용 기저귀도 동일",
                "위생용품(생리대 등)도 같은 방법"
            )
        ),
        RecycleItem(
            id = "broken_glass",
            name = "깨진 유리",
            category = Category.GENERAL,
            disposalMethod = "신문지나 두꺼운 종이로 감싸고 '깨진 유리' 표시 후 종량제 봉투에 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "유리병과 다르게 재활용 불가",
                "수거 작업자 안전을 위해 반드시 감싸기",
                "거울·내열유리(냄비 뚜껑)도 일반쓰레기"
            )
        ),
        RecycleItem(
            id = "cigarette",
            name = "담배꽁초",
            category = Category.GENERAL,
            disposalMethod = "불을 완전히 끄고 종량제 봉투에 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "캔이나 페트병에 넣지 않기 (재활용 오염)",
                "전자담배 기기·카트리지는 폐전자제품으로 별도 배출",
                "재떨이에 물을 부어 완전 소화 확인"
            )
        ),
        RecycleItem(
            id = "ceramic",
            name = "도자기·사기그릇",
            category = Category.GENERAL,
            disposalMethod = "신문지로 감싸서 종량제 봉투에 배출 (큰 것은 대형폐기물)",
            disposalLocation = "일반쓰레기 종량제 봉투 / 대형폐기물",
            cautions = listOf(
                "유리류 수거함에 넣지 않기 (재활용 공정 방해)",
                "깨진 조각은 안전하게 감싸서 배출",
                "화분·항아리 등도 동일"
            )
        ),
        RecycleItem(
            id = "wet_tissue",
            name = "물티슈",
            category = Category.GENERAL,
            disposalMethod = "종량제 봉투에 넣어 일반쓰레기로 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "물에 녹지 않으므로 변기에 버리지 않기",
                "아기 물티슈·청소용 물티슈 모두 동일",
                "물티슈 뚜껑(플라스틱)은 분리하여 플라스틱류로"
            )
        ),

        // ───────── 음식물쓰레기 ─────────
        RecycleItem(
            id = "food_waste",
            name = "음식물쓰레기",
            category = Category.FOOD,
            disposalMethod = "물기를 최대한 제거하고 음식물 전용 봉투·수거함에 배출",
            disposalLocation = "음식물쓰레기 전용 수거함",
            cautions = listOf(
                "뼈·조개껍데기·달걀껍데기는 일반쓰레기",
                "이쑤시개·비닐 등 이물질 제거",
                "과일 씨앗·티백은 일반쓰레기로 분리"
            )
        ),
        RecycleItem(
            id = "fruit_peel",
            name = "과일 껍질",
            category = Category.FOOD,
            disposalMethod = "물기를 빼고 음식물쓰레기 전용 봉투에 배출",
            disposalLocation = "음식물쓰레기 전용 수거함",
            cautions = listOf(
                "수박·멜론 등 큰 껍질은 잘게 잘라서 배출",
                "코코넛·파인애플 껍질은 일반쓰레기 (딱딱한 껍질)",
                "바나나 껍질·사과 껍질 등은 음식물쓰레기"
            )
        ),
        RecycleItem(
            id = "leftover_rice",
            name = "남은 밥·반찬",
            category = Category.FOOD,
            disposalMethod = "물기를 빼고 음식물쓰레기 전용 봉투에 배출",
            disposalLocation = "음식물쓰레기 전용 수거함",
            cautions = listOf(
                "이쑤시개·비닐 등 이물질 반드시 제거",
                "장기간 방치하면 악취 발생 — 빨리 배출",
                "국물은 싱크대에 버리고 건더기만 배출"
            )
        ),

        // ───────── 유해폐기물 ─────────
        RecycleItem(
            id = "battery",
            name = "건전지",
            category = Category.HAZARDOUS,
            disposalMethod = "전용 수거함에 배출 (절대 일반쓰레기로 버리지 않음)",
            disposalLocation = "주민센터·아파트 단지 폐건전지 수거함",
            cautions = listOf(
                "방전된 건전지도 반드시 수거함에 배출",
                "충전지·보조배터리도 함께 배출 가능",
                "누액이 있으면 비닐에 밀봉 후 배출"
            )
        ),
        RecycleItem(
            id = "fluorescent",
            name = "형광등",
            category = Category.HAZARDOUS,
            disposalMethod = "깨지지 않게 전용 수거함에 배출",
            disposalLocation = "주민센터·아파트 단지 폐형광등 수거함",
            cautions = listOf(
                "깨진 형광등은 신문지에 싸서 일반쓰레기로",
                "LED 조명은 폐전자제품으로 별도 배출",
                "수은 포함 — 반드시 전용 수거함 이용"
            )
        ),
        RecycleItem(
            id = "medicine",
            name = "폐의약품",
            category = Category.HAZARDOUS,
            disposalMethod = "약국 또는 주민센터 폐의약품 수거함에 배출",
            disposalLocation = "가까운 약국·주민센터 폐의약품 수거함",
            cautions = listOf(
                "알약은 포장 그대로, 물약은 용기째 배출",
                "주사기·수액 세트는 의료폐기물 — 병원에 반납",
                "유통기한 지난 약도 동일하게 배출"
            )
        ),
        RecycleItem(
            id = "paint",
            name = "페인트·시너",
            category = Category.HAZARDOUS,
            disposalMethod = "뚜껑을 열어 완전히 굳힌 후 일반쓰레기 또는 전용 수거",
            disposalLocation = "주민센터 유해폐기물 수거함 / 전용 업체",
            cautions = listOf(
                "액체 상태로 하수구에 절대 버리지 않기",
                "소량은 신문지에 흡수시켜 일반쓰레기로 가능",
                "스프레이 페인트 캔은 내용물 소진 후 캔류로"
            )
        ),
        RecycleItem(
            id = "thermometer",
            name = "수은 체온계",
            category = Category.HAZARDOUS,
            disposalMethod = "깨지지 않게 주의하여 전용 수거함에 배출",
            disposalLocation = "주민센터·약국 유해폐기물 수거함",
            cautions = listOf(
                "깨진 경우 수은에 직접 접촉하지 않기",
                "비닐에 밀봉하여 수거함에 배출",
                "디지털 체온계는 폐전자제품으로 배출"
            )
        ),

        // ───────── 대형폐기물 ─────────
        RecycleItem(
            id = "furniture",
            name = "가구",
            category = Category.LARGE,
            disposalMethod = "관할 구청 또는 주민센터에 인터넷·전화 신고 후 스티커 부착하여 배출",
            disposalLocation = "집 앞 지정 장소 (수거 예약 필요)",
            cautions = listOf(
                "수거 스티커 없이 배출하면 과태료 부과",
                "인터넷 신청: 구청 홈페이지 → 대형폐기물 배출 신청",
                "재사용 가능한 가구는 기부·당근마켓 활용 추천"
            )
        ),
        RecycleItem(
            id = "mattress",
            name = "매트리스",
            category = Category.LARGE,
            disposalMethod = "구청에 대형폐기물 배출 신고 후 스티커 부착하여 배출",
            disposalLocation = "집 앞 지정 장소 (수거 예약 필요)",
            cautions = listOf(
                "스프링 매트리스는 수거 비용이 높은 편",
                "라텍스·메모리폼 매트리스는 비용이 다를 수 있음",
                "배출 전 커버 제거 시 일반쓰레기로 분리 가능"
            )
        ),
        RecycleItem(
            id = "appliance",
            name = "가전제품",
            category = Category.LARGE,
            disposalMethod = "무상방문수거 서비스(1599-0903) 또는 대형폐기물 신고 후 배출",
            disposalLocation = "무상방문수거 또는 집 앞 지정 장소",
            cautions = listOf(
                "냉장고·세탁기·TV·에어컨은 무상방문수거 가능",
                "소형 가전(드라이기·다리미 등)은 폐전자제품 수거함",
                "수거 전 개인 정보 삭제 (PC·스마트폰 등)"
            )
        ),
        RecycleItem(
            id = "bicycle",
            name = "자전거",
            category = Category.LARGE,
            disposalMethod = "구청에 대형폐기물 배출 신고 후 스티커 부착하여 배출",
            disposalLocation = "집 앞 지정 장소 (수거 예약 필요)",
            cautions = listOf(
                "상태가 좋으면 기부·중고거래 추천",
                "타이어만 따로 버릴 경우도 대형폐기물",
                "킥보드·유모차도 동일한 방법"
            )
        ),

        // ───────── 자주 헷갈리는 품목 ─────────
        RecycleItem(
            id = "pizza_box",
            name = "피자박스",
            category = Category.GENERAL,
            disposalMethod = "기름이 배어 재활용 불가 — 종량제 봉투에 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "기름이 묻지 않은 윗판은 종이류 재활용 가능",
                "치킨박스와 동일한 기준",
                "기름 묻은 종이는 모두 일반쓰레기"
            )
        ),
        RecycleItem(
            id = "ramen_cup",
            name = "컵라면 용기",
            category = Category.RECYCLABLE,
            disposalMethod = "국물을 비우고 깨끗이 헹군 후 스티로폼류 또는 플라스틱류로 배출",
            disposalLocation = "스티로폼 또는 플라스틱 수거함",
            cautions = listOf(
                "기름기·국물이 남아있으면 일반쓰레기",
                "종이 재질 컵라면은 일반쓰레기 (코팅 때문)",
                "비닐 뚜껑·젓가락은 분리"
            )
        ),
        RecycleItem(
            id = "coffee_capsule",
            name = "커피캡슐",
            category = Category.GENERAL,
            disposalMethod = "알루미늄·플라스틱 복합 재질이라 종량제 봉투에 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "일부 브랜드(네스프레소 등)는 자체 수거 프로그램 운영",
                "커피 찌꺼기는 음식물쓰레기",
                "브랜드 수거함이 있다면 그쪽으로 반납 추천"
            )
        ),
        RecycleItem(
            id = "ice_pack",
            name = "아이스팩",
            category = Category.GENERAL,
            disposalMethod = "내용물(젤)은 종량제 봉투, 외피는 비닐류로 분리 배출",
            disposalLocation = "일반쓰레기 종량제 봉투 / 비닐류 수거함",
            cautions = listOf(
                "젤 내용물을 하수구에 버리지 않기 (수질 오염)",
                "물로만 된 아이스팩은 물을 버리고 비닐만 배출",
                "재사용 가능하면 중고 나눔·당근마켓 활용"
            )
        ),
        RecycleItem(
            id = "mask",
            name = "마스크",
            category = Category.GENERAL,
            disposalMethod = "끈을 잘라 종량제 봉투에 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "재활용 불가 — 부직포·필터 복합 재질",
                "귀걸이 끈을 잘라야 야생동물 피해 방지",
                "감염 우려 시 밀봉하여 배출"
            )
        ),
        RecycleItem(
            id = "umbrella",
            name = "우산",
            category = Category.GENERAL,
            disposalMethod = "천과 뼈대를 분리 — 천은 일반쓰레기, 금속 뼈대는 고철류",
            disposalLocation = "일반쓰레기 종량제 봉투 / 고철류 수거함",
            cautions = listOf(
                "분리가 어려우면 통째로 종량제 봉투",
                "비닐 우산도 동일하게 배출",
                "소형이면 종량제 봉투, 대형이면 대형폐기물 신고"
            )
        ),
        RecycleItem(
            id = "toothbrush",
            name = "칫솔",
            category = Category.GENERAL,
            disposalMethod = "종량제 봉투에 넣어 일반쓰레기로 배출",
            disposalLocation = "일반쓰레기 종량제 봉투",
            cautions = listOf(
                "플라스틱+나일론 복합 재질이라 재활용 불가",
                "전동칫솔 본체는 폐전자제품 수거함",
                "전동칫솔 헤드(교체 브러시)는 일반쓰레기"
            )
        ),
        RecycleItem(
            id = "small_electronics",
            name = "소형 전자제품",
            category = Category.RECYCLABLE,
            disposalMethod = "주민센터·대형마트 폐전자제품 수거함에 배출",
            disposalLocation = "폐전자제품 수거함 / 무상방문수거(1599-0903)",
            cautions = listOf(
                "드라이기·다리미·선풍기·충전기 등 해당",
                "배터리가 있으면 분리 후 배터리는 폐건전지 수거함",
                "스마트폰·노트북은 개인정보 삭제 후 배출"
            )
        ),
        RecycleItem(
            id = "cooking_oil",
            name = "식용유",
            category = Category.GENERAL,
            disposalMethod = "신문지·키친타올에 흡수시킨 후 종량제 봉투에 배출",
            disposalLocation = "일반쓰레기 종량제 봉투 / 폐식용유 수거함",
            cautions = listOf(
                "하수구에 절대 버리지 않기 (배관 막힘·수질 오염)",
                "주민센터에 폐식용유 수거함이 있는 경우 페트병에 담아 배출",
                "소량은 신문지에 흡수시켜 일반쓰레기로"
            )
        )
    )

    /**
     * 품목명으로 검색. 부분 일치 지원.
     */
    fun search(query: String): List<RecycleItem> {
        if (query.isBlank()) return emptyList()
        return items.filter { it.name.contains(query.trim()) }
    }

    /**
     * 우리 동네 배출 일정 (mock).
     */
    val regionSchedule = RegionSchedule(
        regionName = "서울특별시 강남구 역삼동",
        schedules = listOf(
            DaySchedule(Category.GENERAL, DayOfWeek.MONDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.MONDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.GENERAL, DayOfWeek.TUESDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.TUESDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.RECYCLABLE, DayOfWeek.WEDNESDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.WEDNESDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.GENERAL, DayOfWeek.THURSDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.THURSDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.RECYCLABLE, DayOfWeek.FRIDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.FRIDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.LARGE, DayOfWeek.SATURDAY, "08:00 ~ 18:00")
        )
    )
}
