
DELETE FROM public.machine;
DELETE FROM public.condominium;
INSERT INTO public.condominium (id, address, contact_phone, email, name) VALUES (55, '123 Main St', '123456789', 'test@test.com', 'Central Park');
INSERT INTO public.machine (id, condominium_id, identifier, type) VALUES (101, 55, 'test-identifier', 'Washer');