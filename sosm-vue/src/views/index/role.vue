<!-- 角色管理 -->
<template>
  <div class="user_html flex-h">
    <div class="search_list">
      <el-button size="mini" type="primary" @click="onAdd">添加</el-button>
    </div>
    <div class="table_box">
      <el-table
        :data="data"
        border
        style="width: 100%"
        height="calc(100vh - 120px)"
      >
        <el-table-column prop="id" align="center" label="序号">
        </el-table-column>
        <el-table-column prop="roleName" align="center" label="角色名">
        </el-table-column>
        <el-table-column prop="roleCode" align="center" label="角色编码">
        </el-table-column>
        <el-table-column align="center" label="操作">
          <template scope="scope">
            <el-button size="mini" @click="onEdit(scope.row.id)"
              >编辑</el-button
            >
            <el-button
              size="mini"
              @click="onPurview(scope.row.id)"
              :disabled="scope.row.roleCode === 'super_admin'"
              >权限</el-button
            >
            <el-popconfirm
              title="这是一段内容确定删除吗？"
              style="margin-left: 10px"
              @confirm="onDel(scope.row.id)"
            >
              <el-button size="mini" slot="reference">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 添加&编辑 -->
    <el-dialog :title="title" :visible.sync="show" v-if="show" width="50%">
      <div>
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="ruleForm"
          label-width="100px"
          class="demo-ruleForm"
        >
          <el-form-item label="角色名" prop="roleName">
            <el-input v-model="ruleForm.roleName"></el-input>
          </el-form-item>
          <el-form-item label="角色编码" prop="roleCode">
            <el-input v-model="ruleForm.roleCode"></el-input>
          </el-form-item>
          <el-form-item label="描述" prop="roledesc">
            <el-input v-model="ruleForm.roledesc"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="show = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="onSet" size="mini">提交</el-button>
      </span>
    </el-dialog>
    <!-- 权限 -->
    <el-dialog title="权限" :visible.sync="show1" v-if="show1" width="50%">
      <div>
        <el-checkbox-group v-model="menuIds">
          <el-checkbox
            v-for="item in menuData"
            :label="item.id"
            :key="item.id"
            >{{ item.menuName }}</el-checkbox
          >
        </el-checkbox-group>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="show1 = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="roleSetMenu" size="mini"
          >提交</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  //import引入的组件需要注入到对象中才能使用
  props: {},
  components: {},
  data() {
    //这里存放数据
    return {
      params: {
        name: "",
      },
      show: false,
      show1: false,
      title: "",
      data: [],
      ruleForm: {
        roleName: "",
        roleCode: "",
        roledesc: "",
      },
      roleId: "",
      menuIds: [],
      menuData: [],
      rules: {
        roleName: [
          { required: true, message: "请输入角色名", trigger: "change" },
        ],
        roleCode: [
          { required: true, message: "请输入角色编码", trigger: "change" },
        ],
        roledesc: [
          { required: true, message: "请输入描述", trigger: "change" },
        ],
      },
    };
  },
  //监听属性 类似于data概念
  computed: {},
  //监控data中的数据变化
  watch: {},
  //方法集合
  methods: {
    // 列表
    findAll() {
      this.$Api.roleFindAll().then((res) => {
        this.data = res.obj;
      });
    },
    // 删除
    onDel(id) {
      this.$Api.roleDelete(id).then((res) => {
        this.$message({
          message: "删除成功",
          type: "success",
        });
        this.findAll();
      });
    },
    // 添加
    onAdd() {
      this.title = "添加";
      this.ruleForm = { roleName: "", roleCode: "", roledesc: "" };
      this.show = true;
    },
    // 编辑
    onEdit(id) {
      this.title = "编辑";
      this.show = true;
      this.$Api.roleFindOne(id).then((res) => {
        this.ruleForm = res.obj;
      });
    },
    // 权限设置窗口
    onPurview(id) {
      this.roleId = id;
      // 获取当前角色菜单
      this.$Api.getMenu(id).then((res) => {
        this.menuIds = res.obj.map((item) => {
          return item.id;
        });
        this.show1 = true;
      });
    },
    // 设置权限
    roleSetMenu() {
      let params = {
        roleId: this.roleId,
        menuIds: this.menuIds,
      };
      this.$Api.roleSetMenu(params).then((res) => {
        this.$message({
          message: "设置成功",
          type: "success",
        });
        this.show1 = false;
      });
    },
    // 编辑&添加
    onSet() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.$Api.roleUpdate(this.ruleForm).then((res) => {
            this.$message({
              message: this.title === "添加" ? "添加成功" : "编辑成功",
              type: "success",
            });
            this.show = false;
            this.findAll();
          });
        }
      });
    },
    // 获取全部菜单
    menuFindAll() {
      this.$Api.menuFindAll().then((res) => {
        this.menuData = res.obj;
      });
    },
  },
  //生命周期 - 创建完成（可以访问当前this实例）
  created() {
    this.menuFindAll();
    this.findAll();
  },
  //生命周期 - 挂载完成（可以访问DOM元素）
  mounted() {},
  beforeCreate() {}, //生命周期 - 创建之前
  beforeMount() {}, //生命周期 - 挂载之前
  beforeUpdate() {}, //生命周期 - 更新之前
  updated() {}, //生命周期 - 更新之后
  beforeDestroy() {}, //生命周期 - 销毁之前
  destroyed() {}, //生命周期 - 销毁完成
  activated() {}, //如果页面有keep-alive缓存功能，这个函数会触发
};
</script>
<style lang='scss' scoped>
//@import url(); 引入公共css类
.user_html {
  padding: 10px 30px;
  flex: 1;

  .table_box {
    width: 100%;
    flex: 1;
    margin-top: 10px;
    overflow: hidden;
  }
}
</style>