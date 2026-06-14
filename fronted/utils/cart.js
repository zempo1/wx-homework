const CART_KEY = 'cart'

function getCart() {
  const cart = wx.getStorageSync(CART_KEY)
  return Array.isArray(cart) ? cart : []
}

function saveCart(cart) {
  wx.setStorageSync(CART_KEY, cart)
  return cart
}

function clearCart() {
  wx.removeStorageSync(CART_KEY)
  return []
}

function normalizeProduct(product) {
  return {
    productId: product.productId || product.id,
    name: product.name,
    price: Number(product.price || 0),
    imageUrl: product.imageUrl || product.productImage || '',
    quantity: Number(product.quantity || 1)
  }
}

function add(product) {
  const cart = getCart()
  const item = normalizeProduct(product)
  const index = cart.findIndex((cartItem) => cartItem.productId === item.productId)

  if (index > -1) {
    cart[index].quantity += item.quantity
  } else {
    cart.push(item)
  }

  return saveCart(cart)
}

function increase(productId) {
  const cart = getCart()
  const item = cart.find((cartItem) => cartItem.productId === productId)
  if (item) {
    item.quantity += 1
  }
  return saveCart(cart)
}

function decrease(productId) {
  const cart = getCart()
  const index = cart.findIndex((cartItem) => cartItem.productId === productId)

  if (index === -1) {
    return cart
  }

  if (cart[index].quantity > 1) {
    cart[index].quantity -= 1
  } else {
    cart.splice(index, 1)
  }

  return saveCart(cart)
}

function remove(productId) {
  return saveCart(getCart().filter((item) => item.productId !== productId))
}

function getCount() {
  return getCart().reduce((total, item) => total + Number(item.quantity || 0), 0)
}

function getTotalPrice() {
  const total = getCart().reduce((sum, item) => {
    return sum + Number(item.price || 0) * Number(item.quantity || 0)
  }, 0)
  return Number(total.toFixed(2))
}

function toOrderItems() {
  return getCart().map((item) => ({
    productId: item.productId,
    quantity: item.quantity
  }))
}

function getCartSummary() {
  const cart = getCart()
  return {
    cart,
    count: getCount(),
    totalPrice: getTotalPrice()
  }
}

module.exports = {
  getCart,
  saveCart,
  clearCart,
  add,
  increase,
  decrease,
  remove,
  getCount,
  getTotalPrice,
  toOrderItems,
  getCartSummary
}
