// src/main/resources/static/admin.js
const { createApp, ref, reactive, onMounted, computed } = Vue;

createApp({
    setup() {
        // — admin credentials —
        const loginForm = reactive({ username:'', password:'' });
        const token     = ref(localStorage.getItem('jwt') || '');
        const isAuth    = computed(() => !!token.value);

        // — product form —
        const form   = reactive({
            name:'', price:'', categoryName:'', subcategoryName:'',
            articleNumber:'', description:'', dimensions:'',
            availability:'AVAILABLE', productionType:'USN_RK',
            imageFile: null
        });
        const message = ref('');

        // load categories & subs
        const categories  = ref([]);
        const subsections = ref([]);
        onMounted(async () => {
            categories.value   = (await (await fetch('/api/categories')).json()).data || [];
            subsections.value  = (await (await fetch('/api/subcategories')).json()).data || [];
        });

        // login as admin, store JWT
        async function doLogin() {
            const res  = await fetch('/api/auth/signin', {
                method: 'POST',
                headers: { 'Content-Type':'application/json' },
                body: JSON.stringify(loginForm)
            });
            const body = await res.json();
            if (res.ok && body.success) {
                token.value = body.data.token;
                localStorage.setItem('jwt', token.value);
                return true;
            } else {
                alert('Не удалось войти: ' + body.message);
                return false;
            }
        }

        // handle file input
        const onFileChange = e => form.imageFile = e.target.files[0];

        // submit new product
        const submit = async () => {
            if (!isAuth.value) {
                const ok = await doLogin();
                if (!ok) return;
            }
            const data = new FormData();
            for (let key of [
                'name','price','categoryName','subcategoryName',
                'articleNumber','description','dimensions',
                'availability','productionType'
            ]) {
                data.append(key, form[key]);
            }
            if (form.imageFile) data.append('file', form.imageFile);

            const res = await fetch('/api/products', {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + token.value },
                body: data
            });
            const result = await res.json();
            message.value = res.ok
                ? 'Товар успешно создан!'
                : 'Ошибка: ' + result.message;
        };

        return {
            // auth
            loginForm, isAuth, doLogin,
            // product form
            form, categories, subsections, onFileChange, submit, message
        };
    },

    template: `
    <!-- LOGIN FORM -->
    <div v-if="!isAuth" class="modal">
      <h2>Вход для администратора</h2>
      <input v-model="loginForm.username" placeholder="Логин" /><br/>
      <input v-model="loginForm.password" type="password" placeholder="Пароль" /><br/>
      <button @click="doLogin">Войти</button>
    </div>

    <!-- CREATE-PRODUCT FORM -->
    <div v-else class="modal">
      <h2>Создать новый товар</h2>
      <div v-if="message">{{ message }}</div>

      <input v-model="form.name"          placeholder="Название" /><br/>
      <input v-model="form.price"         placeholder="Цена" type="number" /><br/>
      <select v-model="form.categoryName">
        <option value="">– Категория –</option>
        <option v-for="c in categories" :key="c.id">{{ c.name }}</option>
      </select><br/>
      <select v-model="form.subcategoryName">
        <option value="">– Подраздел –</option>
        <option v-for="s in subsections" :key="s.id">{{ s.name }}</option>
      </select><br/>
      <input v-model="form.articleNumber" placeholder="Артикул" /><br/>
      <textarea v-model="form.description" placeholder="Описание"></textarea><br/>
      <input v-model="form.dimensions"     placeholder="Размеры" /><br/>
      <select v-model="form.availability">
         <option value="AVAILABLE">В наличии</option>
         <option value="NOT_PRODUCED">Не производится</option>
         <option value="RESERVED">В резерве</option>
      </select><br/>
      <input v-model="form.productionType" placeholder="ProductionType" /><br/>
      <input type="file" @change="onFileChange" /><br/>

      <button @click="submit">Создать</button>
    </div>
  `
}).mount('#adminApp');
