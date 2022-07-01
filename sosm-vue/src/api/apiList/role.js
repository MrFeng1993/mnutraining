import Request from "../request";//引入封装好的公共请求

export default {
  /**
  * 角色列表
  */
  roleFindAll: () => {
    return Request({
      url: "role/findAll",
      method: "get",
    })
  },
  /**
  * 角色详情
  */
  roleFindOne: id => {
    return Request({
      url: `role/findOne/${id}`,
      method: "get",
    })
  },
  /**
  * 添加&修改角色
  */
  roleUpdate: params => {
    return Request({
      url: "role/update",
      method: "post",
      data: params
    })
  },
  /**
  * 删除角色
  */
  roleDelete: id => {
    return Request({
      url: `role/delete/${id}`,
      method: "delete"
    })
  },
  /**
  * 设置菜单
  */
  roleSetMenu: params => {
    return Request({
      url:"role/setMenu",
      method: "post",
      data:params
    })
  },
}