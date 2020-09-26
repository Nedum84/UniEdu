package com.uniedu.model


class QSearchParam (
    val school_id:Int,
    val course_id: String = "",
    val answered:String = "",//1->unanswered, 2->answered, empty->all
    val record_per_page:String = "",
    val start_from:Int = 0
)
