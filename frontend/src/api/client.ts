import type { ApiErrorBody, AssessmentFilters, AssessmentPage, RiskAssessment } from './types'

const baseUrl = (import.meta.env.VITE_API_URL ?? 'http://localhost:8082').replace(/\/$/, '')

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly code?: string,
    readonly correlationId?: string,
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

export interface AuthTokenProvider {
  token?: string
  updateToken(minValidity: number): Promise<boolean>
}

async function request<T>(path: string, auth: AuthTokenProvider, signal?: AbortSignal): Promise<T> {
  await auth.updateToken(30)
  const correlationId = crypto.randomUUID()
  const response = await fetch(`${baseUrl}${path}`, {
    headers: {
      Accept: 'application/json',
      Authorization: `Bearer ${auth.token ?? ''}`,
      'X-Correlation-ID': correlationId,
    },
    signal,
  })

  const responseCorrelationId = response.headers.get('X-Correlation-ID') ?? undefined
  const body = (await response.json().catch(() => null)) as ApiErrorBody | T | null
  if (!response.ok) {
    const errorBody = (body ?? {}) as ApiErrorBody
    throw new ApiError(
      errorBody.message ?? `La solicitud falló con HTTP ${response.status}.`,
      response.status,
      errorBody.code,
      errorBody.correlationId ?? responseCorrelationId,
    )
  }
  return body as T
}

export const assessmentApi = {
  list(
    filters: AssessmentFilters,
    auth: AuthTokenProvider,
    signal?: AbortSignal,
  ): Promise<AssessmentPage> {
    const query = new URLSearchParams({ page: String(filters.page), size: String(filters.size) })
    if (filters.decision) query.set('decision', filters.decision)
    if (filters.riskBand) query.set('riskBand', filters.riskBand)
    return request(`/api/v1/risk-assessments?${query.toString()}`, auth, signal)
  },

  get(id: string, auth: AuthTokenProvider, signal?: AbortSignal): Promise<RiskAssessment> {
    return request(`/api/v1/risk-assessments/${encodeURIComponent(id)}`, auth, signal)
  },
}
