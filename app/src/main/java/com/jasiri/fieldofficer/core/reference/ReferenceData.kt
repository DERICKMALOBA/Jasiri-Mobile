package com.jasiri.fieldofficer.core.reference

/**
 * Static taxonomy mirrored from the web app (D:\projects\jasiri\client\src), shared by the
 * Lead and Customer forms so both match it — LeadFormModal.jsx and registry/components/Form.jsx
 * each define their own copy of this same industry/business-type taxonomy, and Form.jsx's
 * KENYA_COUNTIES spelling (no hyphens) is the one also used by the real customer-creation
 * endpoint's county/business_county columns, so that spelling is authoritative here.
 */
const val OTHER_OPTION = "Other…"

val INDUSTRY_BUSINESS_TYPES: Map<String, List<String>> = linkedMapOf(
    "Retail" to listOf(
        "Clothing Shop", "Second-hand Clothes (Mtumba)", "Electronics Shop", "Grocery Shop",
        "Supermarket", "Cosmetics Shop", "Hardware Shop", "Furniture Shop", "Mobile Phone Shop",
        "General Shop (Kiosk)"
    ),
    "Hospitality & Entertainment" to listOf(
        "Bar", "Restaurant", "Hotel", "Club", "Café", "Lounge", "Fast Food Outlet"
    ),
    "Agriculture" to listOf(
        "Crop Farming", "Dairy Farming", "Poultry Farming", "Fish Farming", "Agro-processing", "Agrovet Shop"
    ),
    "Education" to listOf(
        "Primary School", "Secondary School", "College", "Training Institute", "Driving School",
        "Daycare / Kindergarten"
    ),
    "Transport & Logistics" to listOf(
        "Matatu Business", "Taxi / Ride-hailing", "Courier Service", "Logistics Company",
        "Truck Transport", "Delivery Services"
    ),
    "Technology" to listOf(
        "Software Development", "SaaS Business", "Cyber Café", "IT Services", "Online Business (E-commerce)"
    ),
    "Financial Services" to listOf(
        "Lending Business", "SACCO", "Microfinance", "Insurance Agency", "Forex Bureau"
    ),
    "Healthcare" to listOf(
        "Clinic", "Pharmacy", "Hospital", "Laboratory", "Medical Supplies Shop"
    ),
    "Real Estate" to listOf(
        "Property Management", "Real Estate Agency", "Rental Business", "Property Development"
    ),
    "Creative & Media" to listOf(
        "Photography", "Videography", "Graphic Design", "Printing Services", "Music Production",
        "Content Creation"
    ),
    "Services" to listOf(
        "Salon / Barber Shop", "Laundry Business", "Cleaning Services", "Repair Shop (Phones/Electronics)",
        "Auto Garage", "Car Wash"
    ),
    "Manufacturing" to listOf(
        "Furniture Production", "Clothing Production", "Food Processing", "Metal Fabrication", "Plastic Production"
    ),
    "Wholesale" to listOf(
        "General Wholesale", "Food Wholesale", "Clothing Wholesale", "Electronics Wholesale", "Hardware Wholesale"
    )
)

val KENYA_COUNTIES: List<String> = listOf(
    "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta", "Garissa", "Wajir",
    "Mandera", "Marsabit", "Isiolo", "Meru", "Tharaka Nithi", "Embu", "Kitui", "Machakos",
    "Makueni", "Nyandarua", "Nyeri", "Kirinyaga", "Murang'a", "Kiambu", "Turkana", "West Pokot",
    "Samburu", "Trans Nzoia", "Uasin Gishu", "Elgeyo Marakwet", "Nandi", "Baringo", "Laikipia",
    "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet", "Kakamega", "Vihiga", "Bungoma", "Busia",
    "Siaya", "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira", "Nairobi"
)

val PREFIX_OPTIONS = listOf("Mr", "Mrs", "Ms", "Dr")
val GENDER_OPTIONS = listOf("Male", "Female")
val MARITAL_STATUS_OPTIONS = listOf("Single", "Married", "Separated/Divorced", "Other")
val RESIDENCE_STATUS_OPTIONS = listOf("Own", "Rent", "Family", "Other")
val YES_NO_OPTIONS = listOf("Yes", "No")
