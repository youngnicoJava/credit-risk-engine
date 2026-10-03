export type Decision = 'APPROVE' | 'REFER' | 'REJECT'
export type RiskBand = 'A' | 'B' | 'C' | 'D' | 'E' | 'UNKNOWN'

export interface ReasonCode {
  code: string
  description: string
}

export interface MoneyValue {
  amount: number
  currency: string
}

export interface ScoreComponent {
  code: string
  points: number
  explanation: string
}

export interface Affordability {
  monthlyIncome: MoneyValue
  existingMonthlyDebt: MoneyValue
  proposedMonthlyInstallment: MoneyValue
  currentDebtToIncomeRatio: number
  projectedDebtToIncomeRatio: number
  disposableIncome: MoneyValue
}

export interface ApplicantProfile {
  monthlyIncome: MoneyValue
  existingMonthlyDebtObligations: MoneyValue
  employmentStatus: string
  employmentTenureMonths: number
}

export interface AssessmentExplanation {
  policyId: string
  policyVersion: string
  eligible: boolean
  eligibilityReasons: ReasonCode[]
  applicant: ApplicantProfile
  requestedAmount: MoneyValue
  termMonths: number
  productType: string
  affordability: Affordability
  riskBand: Exclude<RiskBand, 'UNKNOWN'>
  scoreComponents: ScoreComponent[]
}

export interface AssessmentSummary {
  assessmentId: string
  loanApplicationId: string
  decision: Decision
  score: number
  riskBand: RiskBand
  policyVersion: string
  evaluatedAt: string
}

export interface RiskAssessment extends AssessmentSummary {
  assessmentRequestId: string
  customerReference: string
  policyId: string
  reasons: ReasonCode[]
  explanation: AssessmentExplanation | null
  correlationId: string
}

export interface AssessmentPage {
  items: AssessmentSummary[]
  totalElements: number
  totalPages: number
  page: number
  size: number
}

export interface AssessmentFilters {
  page: number
  size: number
  decision?: Decision | ''
  riskBand?: Exclude<RiskBand, 'UNKNOWN'> | ''
}

export interface ApiErrorBody {
  code?: string
  message?: string
  timestamp?: string
  correlationId?: string
}
