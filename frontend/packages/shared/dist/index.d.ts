import * as axios from 'axios';

declare const service: axios.AxiosInstance;

declare const storage: {
    set(key: string, value: any): void;
    get<T = any>(key: string): T | null;
    remove(key: string): void;
    clear(): void;
};
declare const format: {
    date(date: string | Date | number): string;
    phone(phone: string): string;
    currency(amount: number, currency?: string): string;
};
declare const validate: {
    email(email: string): boolean;
    phone(phone: string): boolean;
    password(password: string): boolean;
};
declare const utils: {
    debounce<T extends (...args: any[]) => any>(func: T, wait: number): (...args: Parameters<T>) => void;
    throttle<T extends (...args: any[]) => any>(func: T, limit: number): (...args: Parameters<T>) => void;
    deepClone<T>(obj: T): T;
};

interface ApiResponse<T = any> {
    success: boolean;
    code?: string;
    message: string;
    data: T;
    timestamp: number;
}
interface PageResponse<T = any> {
    data: T[];
    total: number;
    page: number;
    size: number;
    pages: number;
}
interface User {
    id: number;
    username: string;
    email?: string;
    phone?: string;
    realName?: string;
    status: 0 | 1;
    createdTime: string;
    updatedTime?: string;
}
interface UserInfo {
    id: number;
    username: string;
    email?: string;
    phone?: string;
    realName?: string;
    avatar?: string;
    roles: string[];
    permissions: string[];
}
interface LoginRequest {
    username: string;
    password: string;
}
interface LoginResponse {
    token: string;
    userInfo: UserInfo;
}
interface TableQuery {
    page?: number;
    size?: number;
    keyword?: string;
    status?: string;
    startTime?: string;
    endTime?: string;
    [key: string]: any;
}
interface Permission {
    id: number;
    code: string;
    name: string;
    description: string;
}
interface RouteMeta {
    title?: string;
    requiresAuth?: boolean;
    roles?: string[];
    permissions?: string[];
}
type StatusType = 'success' | 'warning' | 'info' | 'danger';

export { ApiResponse, LoginRequest, LoginResponse, PageResponse, Permission, RouteMeta, StatusType, TableQuery, User, UserInfo, format, service as request, storage, utils, validate };
