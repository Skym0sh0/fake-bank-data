export function denormalizeUser(u) {
    if (!u)
        return null

    return {
        id: u.id,
        username: u.username,
        firstname: u.firstname,
        lastname: u.lastname,
    }
}

export function normalizeTransaction(t) {
    if (!t)
        return null

    return {
        id: t.id,
        date: t.date,
        amountInCents: t.amount,
        isPeriodic: t.isPeriodic,
        categoryId: t.category,
    }
}

export function denormalizeTransaction(t) {
    if (!t)
        return null

    return {
        id: t.id,
        date: t.date,
        amount: t.amountInCents,
        isPeriodic: t.isPeriodic,
        category: t.categoryId,
    }
}

export function normalizeStatement(stmt) {
    if (!stmt)
        return null

    const transactions = (stmt.transactions || []).map(t => normalizeTransaction(t))

    return {
        id: stmt.id,
        date: stmt.date,
        finalBalanceInCents: parseInt(stmt.balance),
        transactions: transactions,
        previousStatementId: (stmt.previousStatement && stmt.previousStatement.id) || null,
        previousBalanceInCents: (stmt.previousStatement && stmt.previousStatement.balance) || null, //hier das klappt nbcihtw enns 0 ist
    }
}

export function denormalizeStatement(stmt) {
    if (!stmt)
        return null

    return {
        id: stmt.id,
        date: stmt.date,
        balance: stmt.finalBalanceInCents,
        transactions: stmt.transactions && stmt.transactions.map(t => denormalizeTransaction(t)),
        previousStatement: denormalizeStatement(stmt.previousStatement),
        volume: stmt.volumeInCents,
    }
}

export function denormalizeReason(rsn) {
    if (!rsn)
        return null

    return {
        ...rsn
    }
}

export function normalizeCategory(cat) {
    if (!cat)
        return null

    return {
        id: cat.id || null,
        parentId: cat.parentId || null,
        name: cat.name,
        description: cat.description,
    }
}

export function denormalizeCategory(cat) {
    return {
        id: cat.id,
        parentId: cat.parentId,
        name: cat.name,
        description: cat.description,
        isNew: cat.isNew,

        subCategories: (cat.subCategories || []).map(denormalizeCategory),

        createdAt: cat.createdAt,
        updatedAt: cat.updatedAt,

        usageCount: cat.usageCount,
    }
}

export function denormalizeTurnoverImport(turnoverImport) {
    return {
        id: turnoverImport.id,
        importedAt: turnoverImport.importedAt,

        filename: turnoverImport.filename,
        filesizeBytes: turnoverImport.filesizeBytes,
        encoding: turnoverImport.encoding,
        format: turnoverImport.format,

        firstTurnover: turnoverImport.firstTurnover,
        lastTurnover: turnoverImport.lastTurnover,

        turnovers: (turnoverImport.turnovers || []).map(denormalizeTurnoverRow),
    };
}

export function denormalizeTurnoverPreview(turnover) {
    return {
        filename: turnover.filename,
        format: turnover.format,
        uploadTime: turnover.uploadTime,
        rows: (turnover.rows || []).map(denormalizeTurnoverPreviewRow)
    };
}

export function denormalizeTurnoverRow(row) {
    return {
        id: row.id,
        date: row.date,
        amountInCents: row.amountInCents,
        categoryId: row.categoryId,
        checksum: row.checksum,
        description: row.description,
        suggestedCategory: row.suggestedCategory,
        recipient: row.recipient,
    }
}

export function denormalizeTurnoverPreviewRow(row) {
    return {
        date: row.date,
        amountInCents: row.amountInCents,
        categoryId: row.categoryId,
        suggestedCategories: (row.suggestedCategories || []).map(denormalizeTurnoverPreviewCategorySuggestion),
        suggestedCategory: row.suggestedCategory,
        description: row.description,
        recipient: row.recipient,
        checksum: row.checksum,
        importable: row.importable,
    };
}

export function denormalizeTurnoverPreviewCategorySuggestion(suggestions) {
    return {
        categoryId: suggestions.categoryId,
        frequency: suggestions.frequency,
    };
}

export function denormalizeBasicReportInfo(info) {
    return {
        userId: info.userId,
        earliestTransaction: info.earliestTransaction,
        latestTransaction: info.latestTransaction,
        numberOfTransactions: info.numberOfTransactions,
        numberOfUsedCategories: info.numberOfUsedCategories,
        maxDepthOfCategories: info.maxDepthOfCategories,
    }
}
