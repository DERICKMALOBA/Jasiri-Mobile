package com.jasiri.fieldofficer.core.leads

/**
 * Mirrors the web app's LeadFormModal.jsx static taxonomy (D:\projects\jasiri\client\src\pages\leads\LeadFormModal.jsx)
 * so the mobile Add Lead form matches it field-for-field. Lead Source, Lending Model and
 * Customer Classification are tenant-configurable there (fetched from /api/lead-settings/...)
 * with no static fallback list, so they aren't reproduced here — mobile defaults Lead Source
 * to "Field Agent" the same way the web form does when nothing is picked.
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
    "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita-Taveta", "Garissa", "Wajir",
    "Mandera", "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi", "Embu", "Kitui", "Machakos",
    "Makueni", "Nyandarua", "Nyeri", "Kirinyaga", "Murang'a", "Kiambu", "Turkana", "West Pokot",
    "Samburu", "Trans Nzoia", "Uasin Gishu", "Elgeyo-Marakwet", "Nandi", "Baringo", "Laikipia",
    "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet", "Kakamega", "Vihiga", "Bungoma", "Busia",
    "Siaya", "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira", "Nairobi"
)
