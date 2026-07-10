import express from 'express'
import request from 'supertest'
import { jest } from '@jest/globals'
import bffRoutes from '../routes/bffRoutes.js'

describe('BFF routes', () => {
  let app

  beforeEach(() => {
    app = express()
    app.use(express.json())
    app.use('/bff', bffRoutes)
    if (global.fetch && global.fetch.mockClear) global.fetch.mockClear()
  })

  test('GET /bff/publicaciones returns publications from publication service', async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => [{ id: 1, nombre: 'Firulais' }]
    })

    const res = await request(app).get('/bff/publicaciones')
    expect(res.status).toBe(200)
    expect(res.body).toEqual([{ id: 1, nombre: 'Firulais' }])
    expect(global.fetch).toHaveBeenCalled()
  })

  test('GET /bff/publicaciones/:id forwards id and returns single publication', async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ id: 2, nombre: 'Rex' })
    })

    const res = await request(app).get('/bff/publicaciones/2')
    expect(res.status).toBe(200)
    expect(res.body).toEqual({ id: 2, nombre: 'Rex' })
    expect(global.fetch).toHaveBeenCalledWith(expect.stringContaining('/api/publicaciones/2'), expect.any(Object))
  })

  test('GET /bff/usuarios forwards Authorization header', async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => [{ id: 1, nombre: 'Juan' }]
    })

    const res = await request(app).get('/bff/usuarios').set('Authorization', 'Bearer token123')
    expect(res.status).toBe(200)
    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), expect.objectContaining({ headers: expect.objectContaining({ Authorization: 'Bearer token123' }) }))
  })

  test('GET /bff/mascotas returns pets from pet service', async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => [{ id: 7, nombre: 'Milo', raza: 'Labrador', estado: 'ACTIVO' }]
    })

    const res = await request(app).get('/bff/mascotas')
    expect(res.status).toBe(200)
    expect(res.body).toEqual([{ id: 7, nombre: 'Milo', raza: 'Labrador', estado: 'ACTIVO' }])
  })

  test('handles upstream error gracefully', async () => {
    global.fetch = jest.fn().mockRejectedValue(new Error('network'))

    const res = await request(app).get('/bff/publicaciones')
    expect(res.status).toBe(502)
    expect(res.body).toHaveProperty('error')
  })
})
