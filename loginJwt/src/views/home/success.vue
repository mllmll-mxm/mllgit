<template>
    <div>
      <h1>登录成功</h1>
      <p>{{this.$cookies.get("username")}}</p>
      <button v-on:click="toHome">返回首页</button>
    </div>
</template>

<script>
    export default {
        name: "success.vue",
      data(){
          return{

          }
      },
      methods:{
        toHome:function () {
          this.$router.push('/');
        }
      }
    }
</script>

<style scoped>

</style>
