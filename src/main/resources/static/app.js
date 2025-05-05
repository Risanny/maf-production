const { createApp, ref, reactive, onMounted, computed } = Vue;

createApp({
    setup() {
        // Данные каталога
        const products            = ref([]);
        const categories          = ref([]);
        const subsections         = ref([]);
        const selectedSection     = ref('');
        const selectedSubsection  = ref('');
        const searchQuery         = ref('');

        // Корзина и модальные окна
        const cart                = reactive([]);
        const showCart            = ref(false);
        const showOrderForm       = ref(false);
        const orderForm           = reactive({ name: '', phone: '', email: '', comment: '' });

        // Детали товара
        const selectedProduct     = ref(null);
        const showDetail          = ref(false);

        // Состояния загрузки и отправки
        const isLoading           = ref(false);
        const isSubmitting        = ref(false);

        // Загрузка данных из API
        const fetchProducts = async () => {
            isLoading.value = true;
            const res        = await fetch('/api/products');
            const json       = await res.json();
            products.value   = json.data || [];
            isLoading.value  = false;
        };
        const fetchCategories = async () => {
            const res         = await fetch('/api/categories');
            const json        = await res.json();
            categories.value  = json.data || [];
        };
        const fetchSubsections = async () => {
            const res         = await fetch('/api/subcategories');
            const json        = await res.json();
            subsections.value = json.data || [];
        };

        // Фильтрация товаров
        const filteredProducts = computed(() =>
            products.value.filter(p =>
                (!selectedSection.value    || p.categoryName === selectedSection.value) &&
                (!selectedSubsection.value || p.subcategoryName === selectedSubsection.value) &&
                (!searchQuery.value        || p.name.toLowerCase().includes(searchQuery.value.toLowerCase()))
            )
        );

        // Работа с корзиной
        const addToCart = prod => cart.push(prod);
        const removeFromCart = idx => cart.splice(idx, 1);

        // Оформление заказа
        const submitOrder = async () => {
            isSubmitting.value = true;
            const payload = {
                name:    orderForm.name,
                phone:   orderForm.phone,
                email:   orderForm.email,
                comment: orderForm.comment,
                items:   cart.map(p => ({ id: p.id, name: p.name, price: p.price }))
            };
            const res = await fetch('/api/order', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            isSubmitting.value = false;
            if (res.ok) {
                alert('Спасибо! Ваш заказ принят.');
                cart.splice(0);
                Object.assign(orderForm, { name:'', phone:'', email:'', comment:'' });
                showOrderForm.value = false;
                showCart.value      = false;
            } else {
                const err = await res.json();
                alert('Ошибка при отправке заказа: ' + (err.message || res.statusText));
            }
        };

        // Работа с деталями товара
        const openDetail  = prod => { selectedProduct.value = prod; showDetail.value = true; };
        const closeDetail = ()   => { selectedProduct.value = null; showDetail.value = false; };

        onMounted(() => {
            fetchProducts();
            fetchCategories();
            fetchSubsections();
        });

        return {
            categories, subsections, filteredProducts,
            selectedSection, selectedSubsection, searchQuery,
            cart, showCart, showOrderForm, orderForm,
            selectedProduct, showDetail,
            isLoading, isSubmitting,
            addToCart, removeFromCart, submitOrder,
            openDetail, closeDetail
        };
    },

    template: `
    <!-- Спиннер во время начальной загрузки -->
    <div class="spinner-overlay" v-if="isLoading">
      <div class="spinner"></div>
    </div>

    <header><h1>Каталог МАФ</h1></header>

    <section class="filters">
      <select v-model="selectedSection">
        <option value="">Все разделы</option>
        <option v-for="c in categories"    :key="c.id" :value="c.name">{{ c.name }}</option>
      </select>

      <select v-model="selectedSubsection">
        <option value="">Все подразделы</option>
        <option v-for="s in subsections" :key="s.id" :value="s.name">{{ s.name }}</option>
      </select>

      <input type="text" v-model="searchQuery" placeholder="Поиск...">
      <button @click="showCart = true">Корзина ({{ cart.length }})</button>
    </section>

    <section class="catalog">
      <div v-if="!filteredProducts.length">Товары не найдены...</div>
      <div class="card" v-for="p in filteredProducts" :key="p.id" @click="openDetail(p)">
        <img :src="p.imageUrl ? '/uploads/' + p.imageUrl : ''" alt="" />
        <h2>{{ p.name }}</h2>
        <p><strong>Цена:</strong> {{ p.price }} ₸</p>
        <p><strong>Наличие:</strong> {{ p.availability }}</p>
        <p><strong>Производство:</strong> {{ p.productionType }}</p>
        <button @click.stop="addToCart(p)">В корзину</button>
      </div>
    </section>

    <!-- Модалка корзины -->
    <div class="modal" v-if="showCart">
      <h2>Ваша корзина</h2>
      <ul>
        <li v-for="(item, i) in cart" :key="i">
          {{ item.name }} — {{ item.price }} ₸
          <button @click="removeFromCart(i)">Удалить</button>
        </li>
      </ul>
      <button @click="showOrderForm = true" :disabled="!cart.length">Оформить заказ</button>
      <button @click="showCart = false">Закрыть</button>
    </div>

    <!-- Модалка оформления заказа -->
    <div class="modal" v-if="showOrderForm">
      <h2>Оформление заказа</h2>
      <form @submit.prevent="submitOrder">
        <input v-model="orderForm.name"    placeholder="Имя"     required />
        <input v-model="orderForm.phone"   placeholder="Телефон" required />
        <input v-model="orderForm.email"   type="email" placeholder="E-mail" required />
        <textarea v-model="orderForm.comment" placeholder="Комментарий"></textarea>
        <button type="submit" :disabled="isSubmitting">
          <span v-if="!isSubmitting">Отправить</span>
          <span v-else>Отправка...</span>
        </button>
      </form>
      <button @click="showOrderForm = false">Отмена</button>
    </div>

    <!-- Модалка деталек товара -->
    <div class="modal detail-modal" v-if="showDetail">
      <h2>{{ selectedProduct.name }}</h2>
      <p><strong>Артикул:</strong> {{ selectedProduct.articleNumber }}</p>
      <p><strong>Категория:</strong> {{ selectedProduct.categoryName }}</p>
      <p><strong>Подкатегория:</strong> {{ selectedProduct.subcategoryName }}</p>
      <p><strong>Наличие:</strong> {{ selectedProduct.availability }}</p>
      <p><strong>Производство:</strong> {{ selectedProduct.productionType }}</p>
      <p><strong>Размеры:</strong> {{ selectedProduct.dimensions }}</p>
      <p><strong>Описание:</strong><br>{{ selectedProduct.description }}</p>
      <button @click="closeDetail">Закрыть</button>
      <button @click.stop="addToCart(selectedProduct)">В корзину</button>
    </div>
  `
}).mount('#app');
