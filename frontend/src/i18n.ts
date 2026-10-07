const codeLabels: Record<string, string> = {
  LOW_DEBT_BURDEN: 'Bajo nivel de deuda',
  HIGH_EXISTING_DTI: 'DTI actual elevado',
  STRONG_AFFORDABILITY: 'Buena capacidad de pago',
  HIGH_PROJECTED_DTI: 'DTI proyectado elevado',
  HEALTHY_DISPOSABLE_INCOME: 'Ingreso disponible holgado',
  ADEQUATE_DISPOSABLE_INCOME: 'Ingreso disponible suficiente',
  LOW_DISPOSABLE_INCOME: 'Ingreso disponible bajo',
  STABLE_EMPLOYMENT: 'Empleo estable',
  LIMITED_EMPLOYMENT_STABILITY: 'Estabilidad laboral limitada',
  ESTABLISHED_SELF_EMPLOYMENT: 'Actividad independiente consolidada',
  LOW_LOAN_TO_INCOME: 'Baja relación préstamo/ingreso',
  HIGH_LOAN_TO_INCOME: 'Alta relación préstamo/ingreso',
  LONG_TERM_EXPOSURE: 'Exposición por plazo prolongado',
  UNSUPPORTED_PRODUCT: 'Producto no admitido',
  UNSUPPORTED_CURRENCY: 'Moneda no admitida',
  AMOUNT_OUTSIDE_POLICY: 'Importe fuera de los límites',
  TERM_OUTSIDE_POLICY: 'Plazo fuera de los límites',
  INSUFFICIENT_INCOME: 'Ingreso insuficiente',
  HIGH_PAYMENT_TO_INCOME: 'Cuota elevada respecto del ingreso',
  SCORE_BELOW_APPROVAL_THRESHOLD: 'Puntaje bajo el umbral de aprobación',
  MANUAL_REVIEW_REQUIRED: 'Requiere revisión manual',
  ELIGIBLE_BY_POLICY: 'Cumple los criterios de elegibilidad',
};
const explanations: Record<string, string> = {
  'Existing debt is at most 10% of income.': 'La deuda actual representa como máximo el 10% del ingreso.',
  'Existing debt is at most 25% of income.': 'La deuda actual representa como máximo el 25% del ingreso.',
  'Existing debt exceeds 25% of income.': 'La deuda actual supera el 25% del ingreso.',
  'Projected DTI is at most 25%.': 'El DTI proyectado es como máximo del 25%.',
  'Projected DTI is at most 35%.': 'El DTI proyectado es como máximo del 35%.',
  'Projected DTI exceeds 35%.': 'El DTI proyectado supera el 35%.',
  'Projected DTI exceeds 50%.': 'El DTI proyectado supera el 50%.',
  'Disposable income is at least three reference installments.': 'El ingreso disponible equivale al menos a tres cuotas de referencia.',
  'Disposable income covers the reference installment.': 'El ingreso disponible cubre la cuota de referencia.',
  'Disposable income is below the reference installment.': 'El ingreso disponible no alcanza la cuota de referencia.',
  'Permanent employment tenure is at least 24 months.': 'La antigüedad en relación de dependencia es de al menos 24 meses.',
  'Permanent employment tenure is under 6 months.': 'La antigüedad en relación de dependencia es menor a 6 meses.',
  'Permanent employment tenure is between 6 and 23 months.': 'La antigüedad en relación de dependencia está entre 6 y 23 meses.',
  'Self-employment tenure is at least 36 months.': 'La antigüedad como trabajador independiente es de al menos 36 meses.',
  'Self-employment tenure is under 36 months.': 'La antigüedad como trabajador independiente es menor a 36 meses.',
  'Employment is temporary.': 'La situación laboral es temporal.',
  'Applicant reports no current employment.': 'La persona declara no tener empleo actualmente.',
  'Requested principal is at most two monthly incomes.': 'El capital solicitado equivale como máximo a dos ingresos mensuales.',
  'Requested principal exceeds five monthly incomes.': 'El capital solicitado supera cinco ingresos mensuales.',
  'Requested term exceeds 48 months.': 'El plazo solicitado supera los 48 meses.',
  'The requested product is not supported by this policy.': 'La política no admite el producto solicitado.',
  'The requested currency is not supported by this policy.': 'La política no admite la moneda solicitada.',
  'The requested amount is outside policy limits.': 'El importe solicitado está fuera de los límites de la política.',
  'The requested term is outside policy limits.': 'El plazo solicitado está fuera de los límites de la política.',
  'Declared monthly income does not meet the policy minimum.': 'El ingreso mensual declarado no alcanza el mínimo de la política.',
  'Projected debt obligations exceed the policy debt-to-income threshold.': 'Las obligaciones proyectadas superan el umbral de DTI de la política.',
  'Disposable income after the reference installment is too low.': 'El ingreso disponible luego de la cuota de referencia es insuficiente.',
  'The reference installment consumes a high share of monthly income.': 'La cuota de referencia representa una parte elevada del ingreso mensual.',
  'Employment tenure or status requires additional review.': 'La situación o antigüedad laboral requiere revisión adicional.',
  'Income and projected obligations indicate strong affordability.': 'El ingreso y las obligaciones proyectadas indican buena capacidad de pago.',
  'Existing monthly debt is low relative to income.': 'La deuda mensual actual es baja en relación con el ingreso.',
  'The internal score is below the automatic approval threshold.': 'El puntaje interno está por debajo del umbral de aprobación automática.',
  'The profile requires review by an authorized loan officer.': 'El perfil requiere revisión de un oficial de préstamos autorizado.',
  'The profile satisfies the policy eligibility and automatic approval rules.': 'El perfil cumple los criterios de elegibilidad y aprobación automática.',
};
const employmentLabels: Record<string, string> = {
  PERMANENT: 'Relación de dependencia',
  SELF_EMPLOYED: 'Trabajo independiente',
  TEMPORARY: 'Empleo temporal',
  UNEMPLOYED: 'Sin empleo',
};
export function riskCodeLabel(code: string) {
  return codeLabels[code] ?? code.replaceAll('_', ' ').toLowerCase();
}
export function riskExplanation(text: string) {
  return explanations[text] ?? text;
}
export function employmentLabel(value: string) {
  return employmentLabels[value] ?? riskCodeLabel(value);
}
