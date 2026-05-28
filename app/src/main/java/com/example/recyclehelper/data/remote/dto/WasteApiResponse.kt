package com.example.recyclehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WasteApiResponse(
    val response: ResponseWrapper
)

data class ResponseWrapper(
    val header: ResponseHeader,
    val body: ResponseBody
)

data class ResponseHeader(
    val resultCode: String,
    val resultMsg: String
)

data class ResponseBody(
    val items: ItemsWrapper?,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class ItemsWrapper(
    val item: List<WasteItemDto>
)

data class WasteItemDto(
    @SerializedName("CTPV_NM") val ctpvNm: String?,
    @SerializedName("SGG_NM") val sggNm: String?,
    @SerializedName("MNG_ZONE_NM") val mngZoneNm: String?,
    @SerializedName("MNG_ZONE_TRGT_RGN_NM") val mngZoneTrgtRgnNm: String?,
    @SerializedName("EMSN_PLC") val emsnPlc: String?,
    @SerializedName("EMSN_PLC_TYPE") val emsnPlcType: String?,

    @SerializedName("LF_WST_EMSN_DOW") val lfWstEmsnDow: String?,
    @SerializedName("LF_WST_EMSN_BGNG_TM") val lfWstEmsnBgngTm: String?,
    @SerializedName("LF_WST_EMSN_END_TM") val lfWstEmsnEndTm: String?,
    @SerializedName("LF_WST_EMSN_MTHD") val lfWstEmsnMthd: String?,

    @SerializedName("FOD_WST_EMSN_DOW") val fodWstEmsnDow: String?,
    @SerializedName("FOD_WST_EMSN_BGNG_TM") val fodWstEmsnBgngTm: String?,
    @SerializedName("FOD_WST_EMSN_END_TM") val fodWstEmsnEndTm: String?,
    @SerializedName("FOD_WST_EMSN_MTHD") val fodWstEmsnMthd: String?,

    @SerializedName("RCYCL_EMSN_DOW") val rcyclEmsnDow: String?,
    @SerializedName("RCYCL_EMSN_BGNG_TM") val rcyclEmsnBgngTm: String?,
    @SerializedName("RCYCL_EMSN_END_TM") val rcyclEmsnEndTm: String?,
    @SerializedName("RCYCL_EMSN_MTHD") val rcyclEmsnMthd: String?,

    @SerializedName("TMPRY_BULK_WASTE_EMSN_BGNG_TM") val bulkBgngTm: String?,
    @SerializedName("TMPRY_BULK_WASTE_EMSN_END_TM") val bulkEndTm: String?,
    @SerializedName("TMPRY_BULK_WASTE_EMSN_MTHD") val bulkMthd: String?,
    @SerializedName("TMPRY_BULK_WASTE_EMSN_PLC") val bulkPlc: String?,

    @SerializedName("UNCLLT_DAY") val unclltDay: String?,
    @SerializedName("MNG_DEPT_NM") val mngDeptNm: String?,
    @SerializedName("MNG_DEPT_TELNO") val mngDeptTelno: String?
)
