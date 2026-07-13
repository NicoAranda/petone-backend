import express from 'express'
import dotenv from 'dotenv'
import cors from 'cors'
import bffRoutes from './routes/bffRoutes.js'

dotenv.config()

const app = express()
const PORT = process.env.PORT || 8082

app.use(cors({
  origin: 'http://localhost:5173',
  methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Authorization', 'Content-Type'],
  credentials: true
}))
app.options('*', cors())
app.use(express.json())

app.use('/bff', bffRoutes)

app.get('/health', (req, res) => res.json({ ok: true, service: 'ms-bff' }))

app.listen(PORT, () => {
  console.log(`ms-bff listening on port ${PORT}`)
})
