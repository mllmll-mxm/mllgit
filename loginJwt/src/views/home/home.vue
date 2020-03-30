<template>
  <div>
      <h1>首页</h1>
      <p v-show="showUser">{{$cookies.get("username")}}</p>
      <button v-show="showLogin" v-on:click="toRegister">注册/登录</button>
      <button v-on:click="toBuy">购买</button>
      <button v-show="showLogout" v-on:click="logout">退出登录</button>
  </div>
</template>

<script>
    import global from "../../assets/js/global";

    export default {
        name: "home.vue",
        inject:['reload'],
        data(){
          return{
            showUser:false,
            showLogout:false,
            showLogin:true,
            isRouterAlive:true
          }
        },
      mounted() {
        if(this.$cookies.get('username')!=null){
          console.log(this.$cookies.get('username'));
          this.showUser = true;
          this.showLogin = false;
          this.showLogout = true;
        }else{
          this.showUser = false;
          this.showLogin = true;
          this.showLogout = false;
        }
      },
      methods: {
        logout: function () {
          let username = this.$cookies.get("username");
          let _this = this;
          this.$axios.get(global.logoutURI, username).then(function (response) {
            let result = response.data;
            if (result.data == "success") {
              localStorage.removeItem("userInfo");
              _this.$cookies.remove('username');
              _this.$cookies.remove('token');
              console.log("退出成功");
              _this.reload();
            } else
              console.log("推出失败");
          });
        },
        toRegister: function () {
          this.$router.push({path: '/login'})
        },
        toBuy: function () {
          const _this = this;
          this.$axios.get(this.global.buyURI).then(function (response) {
            let result = response.data;
            if (result.code == 401) {
              _this.$router.push({path: '/login'});
            }else{
              console.log(result.data);
            }
          });
        }
      }
    }
</script>

<style scoped>

</style>
