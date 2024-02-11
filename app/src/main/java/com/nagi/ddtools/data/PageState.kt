package com.nagi.ddtools.data

//用来区分页面打开的情况
//CREATE:新建
//PREVIEW:预览
//EDIT:编辑
//VIEW:查看
enum class PageState {
    //新建
    CREATE,
    PREVIEW,
    EDIT,
    VIEW
}