// 存储工具
export const storage = {
  set(key: string, value: any) {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (e) {
      console.error('Failed to set item to localStorage', e)
    }
  },

  get<T = any>(key: string): T | null {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : null
    } catch (e) {
      console.error('Failed to get item from localStorage', e)
      return null
    }
  },

  remove(key: string) {
    localStorage.removeItem(key)
  },

  clear() {
    localStorage.clear()
  }
}

// 格式化工具
export const format = {
  // 格式化日期
  date(date: string | Date | number) {
    // 这里可以使用 dayjs 或其他日期库
    return new Date(date).toLocaleString()
  },

  // 格式化手机号
  phone(phone: string) {
    return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  },

  // 格式化金额
  currency(amount: number, currency = '¥') {
    return `${currency}${amount.toFixed(2)}`
  }
}

// 验证工具
export const validate = {
  // 验证邮箱
  email(email: string) {
    const reg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return reg.test(email)
  },

  // 验证手机号
  phone(phone: string) {
    const reg = /^1[3-9]\d{9}$/
    return reg.test(phone)
  },

  // 验证密码强度
  password(password: string) {
    // 至少8位，包含字母和数字
    const reg = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/
    return reg.test(password)
  }
}

// 其他工具函数
export const utils = {
  // 防抖函数
  debounce<T extends (...args: any[]) => any>(func: T, wait: number): (...args: Parameters<T>) => void {
    let timeout: NodeJS.Timeout | null = null
    return function (this: any, ...args: Parameters<T>) {
      if (timeout) {
        clearTimeout(timeout)
      }
      timeout = setTimeout(() => func.apply(this, args), wait)
    }
  },

  // 节流函数
  throttle<T extends (...args: any[]) => any>(func: T, limit: number): (...args: Parameters<T>) => void {
    let inThrottle: boolean
    return function (this: any, ...args: Parameters<T>) {
      if (!inThrottle) {
        func.apply(this, args)
        inThrottle = true
        setTimeout(() => inThrottle = false, limit)
      }
    }
  },

  // 深拷贝
  deepClone<T>(obj: T): T {
    if (obj === null || typeof obj !== 'object') {
      return obj
    }

    if (obj instanceof Date) {
      return new Date(obj.getTime()) as any
    }

    if (obj instanceof Array) {
      return obj.map(item => this.deepClone(item)) as any
    }

    if (typeof obj === 'object') {
      const clonedObj = {} as T
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
          clonedObj[key] = this.deepClone(obj[key])
        }
      }
      return clonedObj
    }

    return obj
  }
}

export default {
  storage,
  format,
  validate,
  utils
}