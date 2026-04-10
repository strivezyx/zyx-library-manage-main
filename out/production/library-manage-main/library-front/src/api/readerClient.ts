import request from '@/utils/request'

export const readerLoginAPI = (params: { phone: string; password: string }) => {
  return request({
    url: '/reader/client/login',
    method: 'post',
    data: params,
  })
}

export const getReaderBookPageAPI = (params: any) => {
  return request({
    url: '/reader/client/book/page',
    method: 'get',
    params,
  })
}

export const getMyBorrowPageAPI = (params: any) => {
  return request({
    url: '/reader/client/borrow/page',
    method: 'get',
    params,
  })
}

export const returnMyBorrowAPI = (id: number) => {
  return request({
    url: `/reader/client/borrow/return/${id}`,
    method: 'put',
  })
}

export const updateReaderPasswordAPI = (params: { oldPwd: string; newPwd: string }) => {
  return request({
    url: '/reader/client/password',
    method: 'put',
    data: params,
  })
}
